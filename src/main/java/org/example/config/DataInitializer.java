package org.example.config;

import org.example.model.WorkflowConfig;
import org.example.repository.WorkflowConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private WorkflowConfigRepository workflowConfigRepository;

    @Override
    public void run(String... args) throws Exception {
        if (workflowConfigRepository.count() == 0) {
            initializeDefaultWorkflow();
        }
    }

    private void initializeDefaultWorkflow() {
        WorkflowConfig defaultWorkflow = new WorkflowConfig();
        defaultWorkflow.setWorkflowName("default_kyc");
        defaultWorkflow.setVersion("1.0");
        defaultWorkflow.setIsActive(true);

        WorkflowConfig.NodeConfig idVerificationNode = new WorkflowConfig.NodeConfig();
        idVerificationNode.setNodeId("id_verification");
        idVerificationNode.setNodeName("Identity Document Verification");
        idVerificationNode.setNodeType("ID_VERIFICATION");
        idVerificationNode.setServiceEndpoint("https://api.thirdparty.com/id-verification");
        idVerificationNode.setRequiredInputs(Collections.singletonList("idCardImage"));
        idVerificationNode.setNextNodes(Collections.singletonList("face_verification"));
        idVerificationNode.setTimeout(30000);

        WorkflowConfig.NodeConfig faceVerificationNode = new WorkflowConfig.NodeConfig();
        faceVerificationNode.setNodeId("face_verification");
        faceVerificationNode.setNodeName("Face Verification and Comparison");
        faceVerificationNode.setNodeType("FACE_VERIFICATION");
        faceVerificationNode.setServiceEndpoint("https://api.thirdparty.com/face-verification");
        faceVerificationNode.setRequiredInputs(Arrays.asList("faceImage", "idCardImage"));
        faceVerificationNode.setNextNodes(Collections.emptyList());
        faceVerificationNode.setTimeout(30000);

        defaultWorkflow.setNodes(Arrays.asList(idVerificationNode, faceVerificationNode));

        workflowConfigRepository.save(defaultWorkflow);
        System.out.println("Default KYC workflow initialized");
    }
}