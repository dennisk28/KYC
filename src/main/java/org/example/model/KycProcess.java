package org.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "kyc_processes")
public class KycProcess {
    @Id
    private String id;
    private String userId;
    private KycStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private IdCardInfo idCardInfo;
    private FaceInfo faceInfo;
    private List<WorkflowNode> workflowNodes;
    private FinalResult finalResult;

    @Data
    public static class IdCardInfo {
        private String fileName;
        private String filePath;
        private LocalDateTime uploadTime;
        private VerificationStatus verificationStatus;
        private Object verificationResult;
    }

    @Data
    public static class FaceInfo {
        private String fileName;
        private String filePath;
        private LocalDateTime uploadTime;
        private VerificationStatus verificationStatus;
        private Object verificationResult;
    }

    @Data
    public static class WorkflowNode {
        private String nodeId;
        private String nodeName;
        private NodeType nodeType;
        private NodeStatus status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Object result;
        private String taskId;
    }

    @Data
    public static class FinalResult {
        private Boolean passed;
        private String reason;
        private Double confidence;
    }

    public enum KycStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }

    public enum VerificationStatus {
        PENDING, SUCCESS, FAILED
    }

    public enum NodeType {
        ID_VERIFICATION, FACE_VERIFICATION, DEEPFAKE_DETECTION
    }

    public enum NodeStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}