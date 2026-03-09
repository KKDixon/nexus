package com.KKDixon.nexus.service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://api.spotify.com/v1";

    private HttpHeaders getHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    public String fetchNowPlaying(String accessToken) {
        try {
            HttpEntity<String> entity =
                    new HttpEntity<>(getHeaders(accessToken));

            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/me/player/currently-playing",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody() != null
                    ? response.getBody()
                    : "{\"playing\": false}";

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch now playing\"}";
        }
    }

    public String fetchRecentlyPlayed(String accessToken) {
        try {
            HttpEntity<String> entity =
                    new HttpEntity<>(getHeaders(accessToken));

            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/me/player/recently-played?limit=10",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch recently played\"}";
        }
    }

    public String fetchTopTracks(String accessToken) {
        try {
            HttpEntity<String> entity =
                    new HttpEntity<>(getHeaders(accessToken));

            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/me/top/tracks?limit=5&time_range=short_term",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch top tracks\"}";
        }
    }
}