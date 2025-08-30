package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiResponse;
import org.example.dto.ThirdPartyCallbackRequest;
import org.example.service.ThirdPartyCallbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/callback")
@RequiredArgsConstructor
public class CallbackController {

    private final ThirdPartyCallbackService callbackService;

    @PostMapping("/id-verification")
    public ResponseEntity<ApiResponse<String>> idVerificationCallback(
            @RequestBody ThirdPartyCallbackRequest request) {
        try {
            callbackService.handleIdVerificationCallback(request);
            return ResponseEntity.ok(ApiResponse.success("Callback processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "CALLBACK_ERROR"));
        }
    }

    @PostMapping("/face-verification")
    public ResponseEntity<ApiResponse<String>> faceVerificationCallback(
            @RequestBody ThirdPartyCallbackRequest request) {
        try {
            callbackService.handleFaceVerificationCallback(request);
            return ResponseEntity.ok(ApiResponse.success("Callback processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "CALLBACK_ERROR"));
        }
    }
}