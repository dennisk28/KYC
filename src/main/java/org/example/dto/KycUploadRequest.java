package org.example.dto;

import lombok.Data;

@Data
public class KycUploadRequest {
    private String userId;
    private String kycId;
}