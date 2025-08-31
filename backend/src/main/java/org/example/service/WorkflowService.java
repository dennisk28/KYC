package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.KycProcess;
import org.example.model.WorkflowConfig;
import org.example.repository.KycProcessRepository;
import org.example.repository.WorkflowConfigRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final KycProcessRepository kycProcessRepository;
    private final WorkflowConfigRepository workflowConfigRepository;
    private final ThirdPartyService thirdPartyService;

    @Async
    public void startWorkflow(String kycId) {
        try {
            Optional<KycProcess> processOpt = kycProcessRepository.findById(kycId);
            if (!processOpt.isPresent()) {
                log.error("KYC process not found: {}", kycId);
                return;
            }

            KycProcess process = processOpt.get();
            process.setStatus(KycProcess.KycStatus.IN_PROGRESS);
            process.setUpdatedTime(LocalDateTime.now());

            Optional<WorkflowConfig> configOpt = workflowConfigRepository
                    .findByWorkflowNameAndIsActive("default_kyc", true);
            
            if (!configOpt.isPresent()) {
                log.error("Default workflow config not found");
                process.setStatus(KycProcess.KycStatus.FAILED);
                kycProcessRepository.save(process);
                return;
            }

            WorkflowConfig config = configOpt.get();
            initializeWorkflowNodes(process, config);
            executeNextNodes(process);
            
        } catch (Exception e) {
            log.error("Error starting workflow for KYC: {}", kycId, e);
        }
    }

    private void initializeWorkflowNodes(KycProcess process, WorkflowConfig config) {
        List<KycProcess.WorkflowNode> nodes = config.getNodes().stream()
                .map(nodeConfig -> {
                    KycProcess.WorkflowNode node = new KycProcess.WorkflowNode();
                    node.setNodeId(nodeConfig.getNodeId());
                    node.setNodeName(nodeConfig.getNodeName());
                    node.setNodeType(KycProcess.NodeType.valueOf(nodeConfig.getNodeType()));
                    node.setStatus(KycProcess.NodeStatus.PENDING);
                    return node;
                })
                .collect(java.util.stream.Collectors.toList());
        
        process.setWorkflowNodes(nodes);
        kycProcessRepository.save(process);
    }

    private void executeNextNodes(KycProcess process) {
        List<KycProcess.WorkflowNode> pendingNodes = process.getWorkflowNodes().stream()
                .filter(node -> node.getStatus() == KycProcess.NodeStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());

        for (KycProcess.WorkflowNode node : pendingNodes) {
            if (canExecuteNode(process, node)) {
                executeNode(process, node);
            }
        }
    }

    private boolean canExecuteNode(KycProcess process, KycProcess.WorkflowNode node) {
        switch (node.getNodeType()) {
            case ID_VERIFICATION:
                return process.getIdCardInfo() != null && 
                       process.getIdCardInfo().getFilePath() != null;
            case FACE_VERIFICATION:
                return process.getFaceInfo() != null && 
                       process.getFaceInfo().getFilePath() != null &&
                       process.getIdCardInfo() != null &&
                       process.getIdCardInfo().getFilePath() != null;
            default:
                return true;
        }
    }

    private void executeNode(KycProcess process, KycProcess.WorkflowNode node) {
        node.setStatus(KycProcess.NodeStatus.IN_PROGRESS);
        node.setStartTime(LocalDateTime.now());
        node.setTaskId(UUID.randomUUID().toString());
        
        kycProcessRepository.save(process);

        switch (node.getNodeType()) {
            case ID_VERIFICATION:
                thirdPartyService.verifyIdCard(process.getIdCardInfo().getFilePath(), node.getTaskId());
                break;
            case FACE_VERIFICATION:
                thirdPartyService.verifyFace(
                    process.getFaceInfo().getFilePath(), 
                    process.getIdCardInfo().getFilePath(), 
                    node.getTaskId()
                );
                break;
            case DEEPFAKE_DETECTION:
                thirdPartyService.detectDeepfake(process.getFaceInfo().getFilePath(), node.getTaskId());
                break;
        }
    }

    public void handleNodeCompletion(String taskId, boolean success, Object result) {
        KycProcess process = kycProcessRepository.findByTaskId(taskId);
        if (process == null) {
            log.error("KYC process not found for taskId: {}", taskId);
            return;
        }

        Optional<KycProcess.WorkflowNode> nodeOpt = process.getWorkflowNodes().stream()
                .filter(node -> taskId.equals(node.getTaskId()))
                .findFirst();

        if (!nodeOpt.isPresent()) {
            log.error("Node not found for taskId: {}", taskId);
            return;
        }

        KycProcess.WorkflowNode node = nodeOpt.get();
        node.setStatus(success ? KycProcess.NodeStatus.COMPLETED : KycProcess.NodeStatus.FAILED);
        node.setEndTime(LocalDateTime.now());
        node.setResult(result);

        process.setUpdatedTime(LocalDateTime.now());
        kycProcessRepository.save(process);

        if (success) {
            executeNextNodes(process);
            checkWorkflowCompletion(process);
        } else {
            process.setStatus(KycProcess.KycStatus.FAILED);
            kycProcessRepository.save(process);
        }
    }

    private void checkWorkflowCompletion(KycProcess process) {
        boolean allCompleted = process.getWorkflowNodes().stream()
                .allMatch(node -> node.getStatus() == KycProcess.NodeStatus.COMPLETED);

        if (allCompleted) {
            process.setStatus(KycProcess.KycStatus.COMPLETED);
            calculateFinalResult(process);
            kycProcessRepository.save(process);
        }
    }

    private void calculateFinalResult(KycProcess process) {
        boolean allPassed = process.getWorkflowNodes().stream()
                .allMatch(node -> {
                    if (node.getResult() instanceof java.util.Map) {
                        java.util.Map<String, Object> result = (java.util.Map<String, Object>) node.getResult();
                        return Boolean.TRUE.equals(result.get("isValid"));
                    }
                    return false;
                });

        KycProcess.FinalResult finalResult = new KycProcess.FinalResult();
        finalResult.setPassed(allPassed);
        finalResult.setReason(allPassed ? "All verifications passed" : "One or more verifications failed");
        finalResult.setConfidence(allPassed ? 0.95 : 0.0);
        
        process.setFinalResult(finalResult);
    }
}