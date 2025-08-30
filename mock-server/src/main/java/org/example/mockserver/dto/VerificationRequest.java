package org.example.mockserver.dto;

import lombok.Data;

@Data
public class VerificationRequest {
    private String taskId;
    private String imageUrl;
    private String callbackUrl;
}