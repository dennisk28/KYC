package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.KycUploadResponse;
import org.example.dto.KycStatusResponse;
import org.example.dto.CreateKycSessionRequest;
import org.example.dto.CreateKycSessionResponse;
import org.example.model.KycProcess;
import org.example.repository.KycProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {

    private final KycProcessRepository kycProcessRepository;
    private final WorkflowService workflowService;
    private final String uploadDir = "uploads/";

    public CreateKycSessionResponse createKycSession(CreateKycSessionRequest request) {
        KycProcess process = new KycProcess();
        process.setUserId(request.getUserId());
        process.setStatus(KycProcess.KycStatus.PENDING);
        process.setCreatedTime(LocalDateTime.now());
        process.setUpdatedTime(LocalDateTime.now());
        
        process = kycProcessRepository.save(process);
        
        CreateKycSessionResponse response = new CreateKycSessionResponse();
        response.setKycId(process.getId());
        response.setUserId(process.getUserId());
        response.setStatus(process.getStatus().toString());
        
        return response;
    }

    public KycUploadResponse uploadIdCard(String kycId, MultipartFile idCardImage) throws IOException {
        Optional<KycProcess> processOpt = kycProcessRepository.findById(kycId);
        if (!processOpt.isPresent()) {
            throw new RuntimeException("KYC process not found: " + kycId);
        }

        String fileName = UUID.randomUUID() + "_" + idCardImage.getOriginalFilename();
        String filePath = saveFile(idCardImage, fileName);

        KycProcess process = processOpt.get();
        KycProcess.IdCardInfo idCardInfo = new KycProcess.IdCardInfo();
        idCardInfo.setFileName(idCardImage.getOriginalFilename());
        idCardInfo.setFilePath(filePath);
        idCardInfo.setUploadTime(LocalDateTime.now());
        idCardInfo.setVerificationStatus(KycProcess.VerificationStatus.PENDING);
        
        process.setIdCardInfo(idCardInfo);
        process.setUpdatedTime(LocalDateTime.now());
        process = kycProcessRepository.save(process);

        KycUploadResponse response = new KycUploadResponse();
        response.setKycId(process.getId());
        response.setUploadId(fileName);

        return response;
    }

    public KycUploadResponse uploadFace(String kycId, MultipartFile faceImage) throws IOException {
        log.info("Looking for KYC process with ID: {}", kycId);
        Optional<KycProcess> processOpt = kycProcessRepository.findById(kycId);
        log.info("KYC process found: {}", processOpt.isPresent());
        if (!processOpt.isPresent()) {
            log.error("KYC process not found: {}", kycId);
            throw new RuntimeException("KYC process not found: " + kycId);
        }

        String fileName = UUID.randomUUID() + "_" + faceImage.getOriginalFilename();
        String filePath = saveFile(faceImage, fileName);

        KycProcess process = processOpt.get();
        KycProcess.FaceInfo faceInfo = new KycProcess.FaceInfo();
        faceInfo.setFileName(faceImage.getOriginalFilename());
        faceInfo.setFilePath(filePath);
        faceInfo.setUploadTime(LocalDateTime.now());
        faceInfo.setVerificationStatus(KycProcess.VerificationStatus.PENDING);
        
        process.setFaceInfo(faceInfo);
        process.setUpdatedTime(LocalDateTime.now());
        kycProcessRepository.save(process);

        workflowService.startWorkflow(kycId);

        KycUploadResponse response = new KycUploadResponse();
        response.setKycId(kycId);
        response.setUploadId(fileName);

        return response;
    }

    public KycStatusResponse getKycStatus(String kycId) {
        Optional<KycProcess> processOpt = kycProcessRepository.findById(kycId);
        if (!processOpt.isPresent()) {
            throw new RuntimeException("KYC process not found: " + kycId);
        }

        KycProcess process = processOpt.get();
        KycStatusResponse response = new KycStatusResponse();
        response.setKycId(kycId);
        response.setStatus(process.getStatus().toString());
        
        if (process.getWorkflowNodes() != null && !process.getWorkflowNodes().isEmpty()) {
            long completedNodes = process.getWorkflowNodes().stream()
                    .mapToLong(node -> node.getStatus() == KycProcess.NodeStatus.COMPLETED ? 1 : 0)
                    .sum();
            response.setProgress((int) (completedNodes * 100 / process.getWorkflowNodes().size()));
            
            Optional<KycProcess.WorkflowNode> currentNode = process.getWorkflowNodes().stream()
                    .filter(node -> node.getStatus() == KycProcess.NodeStatus.IN_PROGRESS)
                    .findFirst();
            if (currentNode.isPresent()) {
                response.setCurrentNode(currentNode.get().getNodeName());
            }
        } else {
            response.setProgress(0);
        }
        
        response.setResult(process.getFinalResult());
        return response;
    }

    private String saveFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }
}