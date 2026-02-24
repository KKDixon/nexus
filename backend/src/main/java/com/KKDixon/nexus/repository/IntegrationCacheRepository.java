package com.KKDixon.nexus.repository;

import com.KKDixon.nexus.model.IntegrationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationCacheRepository extends JpaRepository<IntegrationCache, UUID> {
    Optional<IntegrationCache> findByUserIdAndIntegration(UUID userId, String integration);
    List<IntegrationCache> findAllByUserId(UUID userId);
    void deleteByUserIdAndIntegration(UUID userId, String integration);
}