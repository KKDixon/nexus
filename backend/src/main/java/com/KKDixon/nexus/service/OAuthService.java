package com.KKDixon.nexus.service;

import com.KKDixon.nexus.model.OAuthToken;
import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.repository.OAuthTokenRepository;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class OAuthService {

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    // ─── Spotify ───────────────────────────────────────────

    public String getSpotifyAuthUrl() {
        String scopes = "user-read-currently-playing " +
                        "user-read-recently-played " +
                        "user-top-read";
        return "https://accounts.spotify.com/authorize" +
                "?client_id=" + spotifyClientId +
                "&response_type=code" +
                "&redirect_uri=http://localhost:8080/api/oauth/spotify/callback" +
                "&scope=" + scopes.replace(" ", "%20");
    }

    public void handleSpotifyCallback(String code, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(spotifyClientId, spotifyClientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri",
                "http://localhost:8080/api/oauth/spotify/callback");

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://accounts.spotify.com/api/token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map<String, Object> tokens = response.getBody();
        saveOAuthToken(user, "spotify", tokens);
    }

    public String refreshSpotifyToken(UUID userId) {

        OAuthToken token = oAuthTokenRepository
                .findByUserIdAndProvider(userId, "spotify")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Spotify not connected"
                ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(spotifyClientId, spotifyClientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", token.getRefreshToken());

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://accounts.spotify.com/api/token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map<String, Object> tokens = response.getBody();
        token.setAccessToken((String) tokens.get("access_token"));
        token.setExpiresAt(LocalDateTime.now().plusSeconds(
                ((Number) tokens.get("expires_in")).longValue()
        ));
        oAuthTokenRepository.save(token);

        return token.getAccessToken();
    }

    // ─── Google ────────────────────────────────────────────

    public String getGoogleAuthUrl() {
        String scopes = "https://www.googleapis.com/auth/calendar.readonly";
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + googleClientId +
                "&response_type=code" +
                "&redirect_uri=http://localhost:8080/api/oauth/google/callback" +
                "&scope=" + scopes.replace("/", "%2F").replace(":", "%3A") +
                "&access_type=offline";
    }

    public void handleGoogleCallback(String code, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri",
                "http://localhost:8080/api/oauth/google/callback");
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map<String, Object> tokens = response.getBody();
        saveOAuthToken(user, "google", tokens);
    }

    public String refreshGoogleToken(UUID userId) {

        OAuthToken token = oAuthTokenRepository
                .findByUserIdAndProvider(userId, "google")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Google not connected"
                ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", token.getRefreshToken());
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map<String, Object> tokens = response.getBody();
        token.setAccessToken((String) tokens.get("access_token"));
        token.setExpiresAt(LocalDateTime.now().plusSeconds(
                ((Number) tokens.get("expires_in")).longValue()
        ));
        oAuthTokenRepository.save(token);

        return token.getAccessToken();
    }

    // ─── Helpers ───────────────────────────────────────────

    public String getValidAccessToken(UUID userId, String provider) {

        OAuthToken token = oAuthTokenRepository
                .findByUserIdAndProvider(userId, provider)
                .orElseThrow(() -> new ResourceNotFoundException(
                        provider + " not connected"
                ));

        if (token.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(5))) {
            if (provider.equals("spotify")) {
                return refreshSpotifyToken(userId);
            } else {
                return refreshGoogleToken(userId);
            }
        }

        return token.getAccessToken();
    }

    private void saveOAuthToken(User user, String provider,
                                 Map<String, Object> tokens) {

        OAuthToken token = oAuthTokenRepository
                .findByUserIdAndProvider(user.getId(), provider)
                .orElse(new OAuthToken());

        token.setUser(user);
        token.setProvider(provider);
        token.setAccessToken((String) tokens.get("access_token"));
        token.setRefreshToken((String) tokens.get("refresh_token"));
        token.setExpiresAt(LocalDateTime.now().plusSeconds(
                ((Number) tokens.get("expires_in")).longValue()
        ));

        oAuthTokenRepository.save(token);
    }

    public void disconnectProvider(UUID userId, String provider) {
        oAuthTokenRepository.deleteByUserIdAndProvider(userId, provider);
    }
}