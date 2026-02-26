package com.KKDixon.nexus.service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchNowPlaying(String accessToken) {
        // TODO: implement Spotify now playing call
        return "{}";
    }

    public String fetchRecentlyPlayed(String accessToken) {
        // TODO: implement Spotify recently played call
        return "{}";
    }

    public String fetchTopTracks(String accessToken) {
        // TODO: implement Spotify top tracks call
        return "{}";
    }
}