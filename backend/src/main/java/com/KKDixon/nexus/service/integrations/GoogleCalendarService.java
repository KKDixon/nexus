package com.KKDixon.nexus.service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GoogleCalendarService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL =
            "https://www.googleapis.com/calendar/v3";

    public String fetchCalendarEvents(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String timeMin = LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
            String timeMax = LocalDateTime.now().plusDays(7)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";

            String url = BASE_URL + "/calendars/primary/events" +
                    "?timeMin=" + timeMin +
                    "&timeMax=" + timeMax +
                    "&singleEvents=true" +
                    "&orderBy=startTime" +
                    "&maxResults=10";

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch calendar events\"}";
        }
    }
}