package org.example.repository;

import org.example.model.KycProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KycProcessRepository extends MongoRepository<KycProcess, String> {
    
    List<KycProcess> findByUserId(String userId);
    
    Page<KycProcess> findByStatus(KycProcess.KycStatus status, Pageable pageable);
    
    Page<KycProcess> findByCreatedTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("{'workflowNodes.taskId': ?0}")
    KycProcess findByTaskId(String taskId);
    
    long countByStatus(KycProcess.KycStatus status);
}