package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyService {

    private final WebClient webClient = WebClient.builder().build();
    
    // Mock服务器URL配置
    private final String idVerificationUrl = "http://localhost:9090/api/verification/id-card";
    private final String faceVerificationUrl = "http://localhost:9090/api/verification/face";
    private final String deepfakeDetectionUrl = "http://localhost:9090/api/verification/deepfake";
    private final String callbackUrl = "http://localhost:8080/api/callback";

    public void verifyIdCard(String filePath, String taskId) {
        Map<String, Object> request = new HashMap<>();
        request.put("taskId", taskId);
        request.put("imageUrl", "http://localhost:8080/api/admin/image/" + extractFileName(filePath));
        request.put("callbackUrl", callbackUrl + "/id-verification");

        webClient.post()
                .uri(idVerificationUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("ID verification request sent successfully for taskId: {}", taskId))
                .doOnError(error -> log.error("Error sending ID verification request for taskId: {}", taskId, error))
                .subscribe();
    }

    public void verifyFace(String faceImagePath, String idCardPath, String taskId) {
        Map<String, Object> request = new HashMap<>();
        request.put("taskId", taskId);
        request.put("imageUrl", "http://localhost:8080/api/admin/image/" + extractFileName(faceImagePath));
        request.put("callbackUrl", callbackUrl + "/face-verification");

        webClient.post()
                .uri(faceVerificationUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Face verification request sent successfully for taskId: {}", taskId))
                .doOnError(error -> log.error("Error sending face verification request for taskId: {}", taskId, error))
                .subscribe();
    }

    public void detectDeepfake(String faceImagePath, String taskId) {
        Map<String, Object> request = new HashMap<>();
        request.put("taskId", taskId);
        request.put("imageUrl", "http://localhost:8080/api/admin/image/" + extractFileName(faceImagePath));
        request.put("callbackUrl", callbackUrl + "/deepfake-detection");

        webClient.post()
                .uri(deepfakeDetectionUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Deepfake detection request sent successfully for taskId: {}", taskId))
                .doOnError(error -> log.error("Error sending deepfake detection request for taskId: {}", taskId, error))
                .subscribe();
    }

    private String extractFileName(String filePath) {
        return filePath.substring(Math.max(filePath.lastIndexOf('\\'), filePath.lastIndexOf('/')) + 1);
    }
}