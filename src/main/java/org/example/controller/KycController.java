package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiResponse;
import org.example.dto.KycUploadResponse;
import org.example.dto.KycStatusResponse;
import org.example.dto.CreateKycSessionRequest;
import org.example.dto.CreateKycSessionResponse;
import org.example.service.KycService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/session")
    public ResponseEntity<ApiResponse<CreateKycSessionResponse>> createKycSession(
            @RequestBody CreateKycSessionRequest request) {
        try {
            CreateKycSessionResponse response = kycService.createKycSession(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "SESSION_CREATE_ERROR"));
        }
    }

    @PostMapping("/{kycId}/upload-id-card")
    public ResponseEntity<ApiResponse<KycUploadResponse>> uploadIdCard(
            @PathVariable String kycId,
            @RequestParam("idCardImage") MultipartFile idCardImage) {
        try {
            KycUploadResponse response = kycService.uploadIdCard(kycId, idCardImage);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "UPLOAD_ERROR"));
        }
    }

    @PostMapping("/{kycId}/upload-face")
    public ResponseEntity<ApiResponse<KycUploadResponse>> uploadFace(
            @PathVariable String kycId,
            @RequestParam("faceImage") MultipartFile faceImage) {
        try {
            KycUploadResponse response = kycService.uploadFace(kycId, faceImage);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "UPLOAD_ERROR"));
        }
    }

    @GetMapping("/{kycId}/status")
    public ResponseEntity<ApiResponse<KycStatusResponse>> getKycStatus(@PathVariable String kycId) {
        try {
            KycStatusResponse response = kycService.getKycStatus(kycId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "STATUS_ERROR"));
        }
    }
}