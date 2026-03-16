package com.KKDixon.nexus.scheduler;

import com.KKDixon.nexus.model.IntegrationCache;
import com.KKDixon.nexus.model.RefreshLog;
import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.model.UserSettings;
import com.KKDixon.nexus.repository.IntegrationCacheRepository;
import com.KKDixon.nexus.repository.RefreshLogRepository;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.repository.UserSettingsRepository;
import com.KKDixon.nexus.repository.OAuthTokenRepository;
import com.KKDixon.nexus.service.OAuthService;
import com.KKDixon.nexus.service.integrations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class IntegrationScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private IntegrationCacheRepository integrationCacheRepository;

    @Autowired
    private RefreshLogRepository refreshLogRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private GithubService githubService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Autowired
    private EventbriteService eventbriteService;

    @Autowired
    private QuoteService quoteService;

    @Scheduled(fixedRate = 1800000) // every 30 minutes
    public void refreshAllIntegrations() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            refreshUserIntegrations(user);
        }
    }

    @Scheduled(cron = "0 0 6 * * *") // every day at 6am
    public void refreshDailyIntegrations() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            refreshQuote(user);
        }
    }

    private void refreshUserIntegrations(User user) {
        userSettingsRepository.findByUserId(user.getId())
                .ifPresent(settings -> {
                    refreshWeather(user, settings);
                    refreshGithub(user, settings);
                    refreshNews(user, settings);
                    refreshSpotify(user);
                    refreshGoogleCalendar(user);
                    refreshEventbrite(user, settings);
                });
    }

    private void refreshWeather(User user, UserSettings settings) {
        long start = System.currentTimeMillis();
        try {
            if (settings.getWeatherCity() == null ||
                settings.getWeatherCity().isBlank()) {
                logRefresh(user, "weather", "skipped", null, 0);
                return;
            }

            String data = weatherService.fetchWeather(
                    settings.getWeatherCity()
            );
            saveCache(user, "weather", data, 30);
            logRefresh(user, "weather", "success", null,
                    (int)(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logRefresh(user, "weather", "failed", e.getMessage(),
                    (int)(System.currentTimeMillis() - start));
        }
    }

    private void refreshGithub(User user, UserSettings settings) {
        long start = System.currentTimeMillis();
        try {
            if (settings.getGithubUsername() == null ||
                settings.getGithubUsername().isBlank()) {
                logRefresh(user, "github", "skipped", null, 0);
                return;
            }

            String data = githubService.fetchGithubData(
                    settings.getGithubUsername()
            );
            saveCache(user, "github", data, 30);
            logRefresh(user, "github", "success", null,
                    (int)(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logRefresh(user, "github", "failed", e.getMessage(),
                    (int)(System.currentTimeMillis() - start));
        }
    }

    private void refreshNews(User user, UserSettings settings) {
        long start = System.currentTimeMillis();
        try {
            if (settings.getNewsTopics() == null ||
                settings.getNewsTopics().isEmpty()) {
                logRefresh(user, "news", "skipped", null, 0);
                return;
            }

            String data = newsService.fetchNews(settings.getNewsTopics());
            saveCache(user, "news", data, 30);
            logRefresh(user, "news", "success", null,
                    (int)(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logRefresh(user, "news", "failed", e.getMessage(),
                    (int)(System.currentTimeMillis() - start));
        }
    }

    private void refreshSpotify(User user) {
        long start = System.currentTimeMillis();
        try {
            boolean connected = oAuthTokenRepository
                    .existsByUserIdAndProvider(user.getId(), "spotify");

            if (!connected) {
                logRefresh(user, "spotify", "skipped", null, 0);
                return;
            }

            String accessToken = oAuthService.getValidAccessToken(
                    user.getId(), "spotify"
            );

            String nowPlaying = spotifyService.fetchNowPlaying(accessToken);
            String recentlyPlayed = spotifyService
                    .fetchRecentlyPlayed(accessToken);
            String topTracks = spotifyService.fetchTopTracks(accessToken);

            saveCache(user, "spotify_now_playing", nowPlaying, 5);
            saveCache(user, "spotify_recently_played", recentlyPlayed, 30);
            saveCache(user, "spotify_top_tracks", topTracks, 30);

            logRefresh(user, "spotify", "success", null,
                    (int)(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logRefresh(user, "spotify", "failed", e.getMessage(),
                    (int)(System.currentTimeMillis() - start));
        }
    }

    private void refreshGoogleCalendar(User user) {
        long start = System.currentTimeMillis();
        try {
            boolean connected = oAuthTokenRepository
                    .existsByUserIdAndProvider(user.getId(), "google");

            if (!connected) {
                logRefresh(user, "google_calendar", "skipped", null, 0);
                return;
            }

            String accessToken = oAuthService.getValidAccessToken(
                    user.getId(), "google"
            );

            String data = googleCalendarService
                    .fetchCalendarEvents(accessToken);
            saveCache(user, "google_calendar", data, 30);

            logRefresh(user, "google_calendar", "success", null,
                    (int)(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logRefresh(user, "google_calendar", "failed", e.getMessage(),
                    (int)(System.currentTimeMillis() - start));
        }
    }

    private void refreshEventbrite(User user, UserSettings settings) {
        long start = System.currentTimeMillis();
        try {
            if (settings.getWeatherCity() == null ||
                settings.getWeatherCity().isBlank()) {
                logRefresh(user, "eventbrite", "skipped", null, 0);
                return;
            }

            String data = eventbriteService.fetchLocalEvents(
                    settings.getWeatherCity()
            );
            saveCache(user, "eventbrite", data, 360);
            logRefresh(user, "eventbrite", "success", null,
                    (int)(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logRefresh(user, "eventbrite", "failed", e.getMessage(),
                    (int)(System.currentTimeMillis() - start));
        }
    }

    private void refreshQuote(User user) {
        long start = System.currentTimeMillis();
        try {
            String data = quoteService.fetchQuoteOfTheDay();
            saveCache(user, "quote", data, 1440);
            logRefresh(user, "quote", "success", null,
                    (int)(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logRefresh(user, "quote", "failed", e.getMessage(),
                    (int)(System.currentTimeMillis() - start));
        }
    }

    private void saveCache(User user, String integration,
                           String data, int expiryMinutes) {
        IntegrationCache cache = integrationCacheRepository
                .findByUserIdAndIntegration(user.getId(), integration)
                .orElse(new IntegrationCache());

        cache.setUser(user);
        cache.setIntegration(integration);
        cache.setData(data);
        cache.setExpiresAt(
                LocalDateTime.now().plusMinutes(expiryMinutes)
        );

        integrationCacheRepository.save(cache);
    }

    private void logRefresh(User user, String integration,
                             String status, String errorMessage,
                             int durationMs) {
        RefreshLog log = new RefreshLog();
        log.setUser(user);
        log.setIntegration(integration);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);
        log.setDurationMs(durationMs);
        refreshLogRepository.save(log);
    }
}