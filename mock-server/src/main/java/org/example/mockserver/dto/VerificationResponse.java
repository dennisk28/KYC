package org.example.mockserver.dto;

import lombok.Data;

@Data
public class VerificationResponse {
    private String taskId;
    private boolean success;
    private String message;
}