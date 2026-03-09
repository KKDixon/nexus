package com.KKDixon.nexus.service;

import com.KKDixon.nexus.dto.DashboardResponse;
import com.KKDixon.nexus.model.IntegrationCache;
import com.KKDixon.nexus.model.UserSettings;
import com.KKDixon.nexus.repository.IntegrationCacheRepository;
import com.KKDixon.nexus.repository.UserSettingsRepository;
import com.KKDixon.nexus.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class DashboardService {

    @Autowired
    private IntegrationCacheRepository integrationCacheRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    public DashboardResponse getDashboardData(UUID userId) {

        UserSettings settings = userSettingsRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User settings not found"
                ));

        DashboardResponse response = new DashboardResponse();

        response.setWeatherData(
            getCachedData(userId, "weather")
        );
        response.setGithubData(
            getCachedData(userId, "github")
        );
        response.setNewsData(
            getCachedData(userId, "news")
        );
        response.setSpotifyNowPlaying(
            getCachedData(userId, "spotify_now_playing")
        );
        response.setSpotifyRecentlyPlayed(
            getCachedData(userId, "spotify_recently_played")
        );
        response.setSpotifyTopTracks(
            getCachedData(userId, "spotify_top_tracks")
        );
        response.setGoogleCalendarData(
            getCachedData(userId, "google_calendar")
        );
        response.setEventbriteData(
            getCachedData(userId, "eventbrite")
        );
        response.setQuoteData(
            getCachedData(userId, "quote")
        );

        response.setLastUpdated(LocalDateTime.now().toString());

        return response;
    }

    private String getCachedData(UUID userId, String integration) {
        Optional<IntegrationCache> cache = integrationCacheRepository
                .findByUserIdAndIntegration(userId, integration);

        if (cache.isEmpty()) {
            return null;
        }

        if (cache.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        return cache.get().getData();
    }
}