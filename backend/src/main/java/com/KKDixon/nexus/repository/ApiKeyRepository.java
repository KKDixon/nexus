package com.KKDixon.nexus.repository;

import com.KKDixon.nexus.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findAllByUserId(UUID userId);
    Optional<ApiKey> findByKeyHash(String keyHash);
    Boolean existsByKeyHash(String keyHash);
}