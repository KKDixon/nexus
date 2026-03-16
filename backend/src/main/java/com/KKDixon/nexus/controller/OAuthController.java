package com.KKDixon.nexus.controller;

import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.service.OAuthService;
import com.KKDixon.nexus.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserRepository userRepository;

    // ─── Spotify ───────────────────────────────────────────

    @GetMapping("/spotify/url")
    public ResponseEntity<Map<String, String>> getSpotifyAuthUrl() {
        String url = oAuthService.getSpotifyAuthUrl();
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/spotify/callback")
    public ResponseEntity<Map<String, String>> handleSpotifyCallback(
            @RequestParam String code) {

        User user = getAuthenticatedUser();
        oAuthService.handleSpotifyCallback(code, user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Spotify connected successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/spotify")
    public ResponseEntity<Map<String, String>> disconnectSpotify() {
        User user = getAuthenticatedUser();
        oAuthService.disconnectProvider(user.getId(), "spotify");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Spotify disconnected successfully");
        return ResponseEntity.ok(response);
    }

    // ─── Google ────────────────────────────────────────────

    @GetMapping("/google/url")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl() {
        String url = oAuthService.getGoogleAuthUrl();
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Map<String, String>> handleGoogleCallback(
            @RequestParam String code) {

        User user = getAuthenticatedUser();
        oAuthService.handleGoogleCallback(code, user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Google Calendar connected successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/google")
    public ResponseEntity<Map<String, String>> disconnectGoogle() {
        User user = getAuthenticatedUser();
        oAuthService.disconnectProvider(user.getId(), "google");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Google Calendar disconnected successfully");
        return ResponseEntity.ok(response);
    }

    // ─── Helper ────────────────────────────────────────────

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