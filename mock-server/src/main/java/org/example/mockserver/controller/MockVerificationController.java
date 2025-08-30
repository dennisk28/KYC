package org.example.mockserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mockserver.dto.CallbackResult;
import org.example.mockserver.dto.VerificationRequest;
import org.example.mockserver.dto.VerificationResponse;
import org.example.mockserver.service.MockVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@Slf4j
public class MockVerificationController {

    private final MockVerificationService verificationService;

    @PostMapping("/id-card")
    public ResponseEntity<VerificationResponse> verifyIdCard(@RequestBody VerificationRequest request) {
        log.info("Received ID card verification request: {}", request.getTaskId());
        
        VerificationResponse response = new VerificationResponse();
        response.setTaskId(request.getTaskId());
        response.setSuccess(true);
        response.setMessage("ID card verification initiated");
        
        verificationService.processIdCardVerification(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/face")
    public ResponseEntity<VerificationResponse> verifyFace(@RequestBody VerificationRequest request) {
        log.info("Received face verification request: {}", request.getTaskId());
        
        VerificationResponse response = new VerificationResponse();
        response.setTaskId(request.getTaskId());
        response.setSuccess(true);
        response.setMessage("Face verification initiated");
        
        verificationService.processFaceVerification(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deepfake")
    public ResponseEntity<VerificationResponse> detectDeepfake(@RequestBody VerificationRequest request) {
        log.info("Received deepfake detection request: {}", request.getTaskId());
        
        VerificationResponse response = new VerificationResponse();
        response.setTaskId(request.getTaskId());
        response.setSuccess(true);
        response.setMessage("Deepfake detection initiated");
        
        verificationService.processDeepfakeDetection(request);
        
        return ResponseEntity.ok(response);
    }
}