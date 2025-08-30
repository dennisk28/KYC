package org.example.mockserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mockserver.dto.CallbackResult;
import org.example.mockserver.service.MockVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final MockVerificationService verificationService;

    @PostMapping("/trigger-callback/{taskId}")
    public ResponseEntity<String> triggerCallback(
            @PathVariable String taskId,
            @RequestParam String type,
            @RequestParam(defaultValue = "true") boolean passed) {
        
        log.info("Manually triggering callback for task: {}, type: {}, passed: {}", taskId, type, passed);
        
        CallbackResult result = new CallbackResult();
        result.setTaskId(taskId);
        result.setStatus("COMPLETED");
        
        CallbackResult.Result verificationResult = new CallbackResult.Result();
        verificationResult.setPassed(passed);
        verificationResult.setConfidence(0.85 + Math.random() * 0.15);
        
        String endpoint;
        switch (type.toLowerCase()) {
            case "id":
            case "id-verification":
                endpoint = "/api/callback/id-verification";
                verificationResult.setReason(passed ? "身份证验证通过" : "身份证信息不匹配");
                break;
            case "face":
            case "face-verification":
                endpoint = "/api/callback/face-verification";
                verificationResult.setReason(passed ? "人脸验证通过" : "人脸与身份证不匹配");
                break;
            case "deepfake":
            case "deepfake-detection":
                endpoint = "/api/callback/deepfake-detection";
                verificationResult.setReason(passed ? "未检测到深度伪造" : "检测到可能的深度伪造");
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid verification type: " + type);
        }
        
        result.setResult(verificationResult);
        
        try {
            org.springframework.web.reactive.function.client.WebClient.builder()
                    .baseUrl("http://localhost:8080")
                    .build()
                    .post()
                    .uri(endpoint)
                    .bodyValue(result)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("Manual callback sent successfully"))
                    .doOnError(error -> log.error("Manual callback failed: {}", error.getMessage()))
                    .subscribe();
            
            return ResponseEntity.ok("Callback triggered successfully");
        } catch (Exception e) {
            log.error("Failed to trigger callback", e);
            return ResponseEntity.internalServerError().body("Failed to trigger callback: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Mock Verification Server",
                "port", "9090"
        ));
    }
}