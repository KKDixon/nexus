package com.KKDixon.nexus.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private String githubData;
    private String weatherData;
    private String newsData;
    private String spotifyNowPlaying;
    private String spotifyRecentlyPlayed;
    private String spotifyTopTracks;
    private String googleCalendarData;
    private String eventbriteData;
    private String quoteData;
    private String lastUpdated;
}