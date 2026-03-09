package com.KKDixon.nexus.service.integrations;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class GithubService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "https://api.github.com";

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    public String fetchGithubData(String username) {
        try {
            HttpEntity<String> entity =
                    new HttpEntity<>(getHeaders());

            ResponseEntity<String> profileResponse = restTemplate.exchange(
                    BASE_URL + "/users/" + username,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ResponseEntity<String> reposResponse = restTemplate.exchange(
                    BASE_URL + "/users/" + username +
                    "/repos?sort=updated&per_page=5",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ResponseEntity<String> eventsResponse = restTemplate.exchange(
                    BASE_URL + "/users/" + username +
                    "/events?per_page=10",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            Map<String, Object> combined = new HashMap<>();
            combined.put("profile",
                    objectMapper.readValue(
                            profileResponse.getBody(), Object.class));
            combined.put("repos",
                    objectMapper.readValue(
                            reposResponse.getBody(), Object.class));
            combined.put("events",
                    objectMapper.readValue(
                            eventsResponse.getBody(), Object.class));

            return objectMapper.writeValueAsString(combined);

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch GitHub data\"}";
        }
    }
}