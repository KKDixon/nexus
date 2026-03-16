package com.KKDixon.nexus.controller;

import com.KKDixon.nexus.dto.DashboardResponse;
import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.service.DashboardService;
import com.KKDixon.nexus.service.ApiKeyService;
import com.KKDixon.nexus.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        User user = getAuthenticatedUser();
        DashboardResponse response =
                dashboardService.getDashboardData(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/me")
    public ResponseEntity<DashboardResponse> getDashboardByApiKey(
            @RequestHeader("X-API-Key") String apiKey) {

        if (!apiKeyService.validateApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }

        User user = getAuthenticatedUser();
        DashboardResponse response =
                dashboardService.getDashboardData(user.getId());
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