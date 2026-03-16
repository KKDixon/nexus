package com.KKDixon.nexus.controller;

import com.KKDixon.nexus.model.ApiKey;
import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.service.ApiKeyService;
import com.KKDixon.nexus.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/keys")
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Map<String, String>> generateKey(
            @RequestParam(required = false) String name) {

        User user = getAuthenticatedUser();

        String rawKey = apiKeyService.generateApiKey(
                user.getId(),
                name != null ? name : "My API Key"
        );

        Map<String, String> response = new HashMap<>();
        response.put("key", rawKey);
        response.put("message",
                "Save this key now. It will not be shown again.");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ApiKey>> listKeys() {
        User user = getAuthenticatedUser();
        List<ApiKey> keys = apiKeyService.getUserApiKeys(user.getId());
        return ResponseEntity.ok(keys);
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Map<String, String>> revokeKey(
            @PathVariable UUID keyId) {

        User user = getAuthenticatedUser();
        apiKeyService.revokeApiKey(keyId, user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "API key revoked successfully");

        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));
    }
}