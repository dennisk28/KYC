package org.example.dto;

import lombok.Data;

@Data
public class ThirdPartyCallbackRequest {
    private String taskId;
    private String status;
    private Object result;
}