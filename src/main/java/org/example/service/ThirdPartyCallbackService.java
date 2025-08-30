package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ThirdPartyCallbackRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyCallbackService {

    private final WorkflowService workflowService;

    public void handleIdVerificationCallback(ThirdPartyCallbackRequest request) {
        log.info("Received ID verification callback for taskId: {}", request.getTaskId());
        
        boolean success = "SUCCESS".equals(request.getStatus());
        workflowService.handleNodeCompletion(request.getTaskId(), success, request.getResult());
    }

    public void handleFaceVerificationCallback(ThirdPartyCallbackRequest request) {
        log.info("Received face verification callback for taskId: {}", request.getTaskId());
        
        boolean success = "SUCCESS".equals(request.getStatus());
        workflowService.handleNodeCompletion(request.getTaskId(), success, request.getResult());
    }
}