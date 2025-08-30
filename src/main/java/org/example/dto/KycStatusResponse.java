package org.example.dto;

import lombok.Data;

@Data
public class KycStatusResponse {
    private String kycId;
    private String status;
    private Integer progress;
    private String currentNode;
    private Object result;
}