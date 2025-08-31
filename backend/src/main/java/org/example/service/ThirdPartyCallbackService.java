package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ThirdPartyCallbackRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyCallbackService {

    private final WorkflowService workflowService;

    public void handleIdVerificationCallback(ThirdPartyCallbackRequest request) {
        log.info("Received ID verification callback for taskId: {}, result: {}", request.getTaskId(),
                request.getResult());
        
        //boolean success = "SUCCESS".equals(request.getStatus());
        boolean success = false;
        if (request.getResult() != null && ((Map)request.getResult()).get("passed") instanceof Boolean) {
            success = (Boolean) ((Map)request.getResult()).get("passed");
        }
        workflowService.handleNodeCompletion(request.getTaskId(), success, request.getResult());
    }

    public void handleFaceVerificationCallback(ThirdPartyCallbackRequest request) {
        log.info("Received face verification callback for taskId: {}, result: {} ", request.getTaskId(),
                request.getResult());
        
        //boolean success = "SUCCESS".equals(request.getStatus());
        boolean success = false;
        if (request.getResult() != null && ((Map)request.getResult()).get("passed") instanceof Boolean) {
            success = (Boolean) ((Map)request.getResult()).get("passed");
        }
        workflowService.handleNodeCompletion(request.getTaskId(), success, request.getResult());
    }
}