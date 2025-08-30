package org.example.mockserver.service;

import lombok.extern.slf4j.Slf4j;
import org.example.mockserver.dto.CallbackResult;
import org.example.mockserver.dto.VerificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MockVerificationService {

    private final WebClient webClient;
    private final Random random = new Random();

    public MockVerificationService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    public void processIdCardVerification(VerificationRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000 + random.nextInt(5000));
                
                CallbackResult result = new CallbackResult();
                result.setTaskId(request.getTaskId());
                result.setStatus("COMPLETED");
                
                CallbackResult.Result verificationResult = new CallbackResult.Result();
                verificationResult.setPassed(random.nextBoolean() ? true : random.nextDouble() > 0.2);
                verificationResult.setReason(verificationResult.isPassed() ? "身份证验证通过" : "身份证信息不匹配");
                verificationResult.setConfidence(0.7 + random.nextDouble() * 0.3);
                
                result.setResult(verificationResult);
                
                sendCallback("/api/callback/id-verification", result);
                
            } catch (Exception e) {
                log.error("ID card verification failed", e);
                sendFailedCallback(request.getTaskId(), "/api/callback/id-verification");
            }
        });
    }

    public void processFaceVerification(VerificationRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000 + random.nextInt(4000));
                
                CallbackResult result = new CallbackResult();
                result.setTaskId(request.getTaskId());
                result.setStatus("COMPLETED");
                
                CallbackResult.Result verificationResult = new CallbackResult.Result();
                verificationResult.setPassed(random.nextBoolean() ? true : random.nextDouble() > 0.15);
                verificationResult.setReason(verificationResult.isPassed() ? "人脸验证通过" : "人脸与身份证不匹配");
                verificationResult.setConfidence(0.8 + random.nextDouble() * 0.2);
                
                result.setResult(verificationResult);
                
                sendCallback("/api/callback/face-verification", result);
                
            } catch (Exception e) {
                log.error("Face verification failed", e);
                sendFailedCallback(request.getTaskId(), "/api/callback/face-verification");
            }
        });
    }

    public void processDeepfakeDetection(VerificationRequest request) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(4000 + random.nextInt(6000));
                
                CallbackResult result = new CallbackResult();
                result.setTaskId(request.getTaskId());
                result.setStatus("COMPLETED");
                
                CallbackResult.Result verificationResult = new CallbackResult.Result();
                verificationResult.setPassed(random.nextDouble() > 0.1);
                verificationResult.setReason(verificationResult.isPassed() ? "未检测到深度伪造" : "检测到可能的深度伪造");
                verificationResult.setConfidence(0.85 + random.nextDouble() * 0.15);
                
                result.setResult(verificationResult);
                
                sendCallback("/api/callback/deepfake-detection", result);
                
            } catch (Exception e) {
                log.error("Deepfake detection failed", e);
                sendFailedCallback(request.getTaskId(), "/api/callback/deepfake-detection");
            }
        });
    }

    private void sendCallback(String endpoint, CallbackResult result) {
        try {
            log.info("Sending callback to {} for task {}", endpoint, result.getTaskId());
            
            webClient.post()
                    .uri(endpoint)
                    .bodyValue(result)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("Callback sent successfully: {}", response))
                    .doOnError(error -> log.error("Callback failed: {}", error.getMessage()))
                    .subscribe();
                    
        } catch (Exception e) {
            log.error("Failed to send callback", e);
        }
    }

    private void sendFailedCallback(String taskId, String endpoint) {
        CallbackResult result = new CallbackResult();
        result.setTaskId(taskId);
        result.setStatus("FAILED");
        
        CallbackResult.Result verificationResult = new CallbackResult.Result();
        verificationResult.setPassed(false);
        verificationResult.setReason("系统错误");
        verificationResult.setConfidence(0.0);
        
        result.setResult(verificationResult);
        
        sendCallback(endpoint, result);
    }
}