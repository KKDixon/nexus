package com.KKDixon.nexus.controller;

import com.KKDixon.nexus.dto.SettingsRequest;
import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.model.UserSettings;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.repository.UserSettingsRepository;
import com.KKDixon.nexus.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserSettings> getSettings() {
        User user = getAuthenticatedUser();

        UserSettings settings = userSettingsRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Settings not found"
                ));

        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<UserSettings> updateSettings(
            @RequestBody SettingsRequest request) {

        User user = getAuthenticatedUser();

        UserSettings settings = userSettingsRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Settings not found"
                ));

        if (request.getGithubUsername() != null) {
            settings.setGithubUsername(request.getGithubUsername());
        }

        if (request.getWeatherCity() != null) {
            settings.setWeatherCity(request.getWeatherCity());
        }

        if (request.getNewsTopics() != null) {
            settings.setNewsTopics(request.getNewsTopics());
        }

        userSettingsRepository.save(settings);

        return ResponseEntity.ok(settings);
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