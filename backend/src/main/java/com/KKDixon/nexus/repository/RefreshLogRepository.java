package com.KKDixon.nexus.repository;

import com.KKDixon.nexus.model.RefreshLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefreshLogRepository extends JpaRepository<RefreshLog, UUID> {
    List<RefreshLog> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    List<RefreshLog> findAllByUserIdAndIntegration(UUID userId, String integration);
    List<RefreshLog> findAllByUserIdAndStatus(UUID userId, String status);
}