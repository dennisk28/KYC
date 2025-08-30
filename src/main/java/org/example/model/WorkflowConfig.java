package org.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "workflow_configs")
public class WorkflowConfig {
    @Id
    private String id;
    private String workflowName;
    private String version;
    private List<NodeConfig> nodes;
    private Boolean isActive;

    @Data
    public static class NodeConfig {
        private String nodeId;
        private String nodeName;
        private String nodeType;
        private String serviceEndpoint;
        private List<String> requiredInputs;
        private List<String> nextNodes;
        private Integer timeout;
    }
}