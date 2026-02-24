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
    private String spotifyData;
    private String googleCalendarData;
    private String lastUpdated;
    private String EventbriteData;
    private String quoteData;
}