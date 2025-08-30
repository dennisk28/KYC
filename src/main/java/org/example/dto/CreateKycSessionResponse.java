package org.example.dto;

import lombok.Data;

@Data
public class CreateKycSessionResponse {
    private String kycId;
    private String userId;
    private String status;
}