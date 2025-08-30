package org.example.repository;

import org.example.model.WorkflowConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowConfigRepository extends MongoRepository<WorkflowConfig, String> {
    
    Optional<WorkflowConfig> findByWorkflowNameAndIsActive(String workflowName, Boolean isActive);
    
    Optional<WorkflowConfig> findByWorkflowNameAndVersion(String workflowName, String version);
}