package com.KKDixon.nexus.repository;

import com.KKDixon.nexus.model.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, UUID> {
    Optional<OAuthToken> findByUserIdAndProvider(UUID userId, String provider);
    Boolean existsByUserIdAndProvider(UUID userId, String provider);
    void deleteByUserIdAndProvider(UUID userId, String provider);
}