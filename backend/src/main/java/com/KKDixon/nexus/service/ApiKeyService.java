package com.KKDixon.nexus.service;

import com.KKDixon.nexus.model.ApiKey;
import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.repository.ApiKeyRepository;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String generateApiKey(UUID userId, String keyName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));

        String rawKey = "nxs_" + UUID.randomUUID().toString().replace("-", "");

        String keyHash = passwordEncoder.encode(rawKey);
        String keyPrefix = rawKey.substring(0, 10);

        ApiKey apiKey = new ApiKey();
        apiKey.setUser(user);
        apiKey.setKeyHash(keyHash);
        apiKey.setKeyPrefix(keyPrefix);
        apiKey.setName(keyName);
        apiKey.setIsActive(true);

        apiKeyRepository.save(apiKey);

        return rawKey;
    }

    public List<ApiKey> getUserApiKeys(UUID userId) {
        return apiKeyRepository.findAllByUserId(userId);
    }

    public void revokeApiKey(UUID keyId, UUID userId) {

        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "API key not found"
                ));

        if (!apiKey.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(
                "You do not own this API key"
            );
        }

        apiKey.setIsActive(false);
        apiKeyRepository.save(apiKey);
    }

    public boolean validateApiKey(String rawKey) {

        List<ApiKey> allKeys = apiKeyRepository.findAll();

        return allKeys.stream()
                .filter(ApiKey::getIsActive)
                .anyMatch(key ->
                    passwordEncoder.matches(rawKey, key.getKeyHash())
                );
    }
}