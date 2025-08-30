package org.example.mockserver.dto;

import lombok.Data;

@Data
public class CallbackResult {
    private String taskId;
    private String status;
    private Result result;

    @Data
    public static class Result {
        private boolean passed;
        private String reason;
        private double confidence;
    }
}