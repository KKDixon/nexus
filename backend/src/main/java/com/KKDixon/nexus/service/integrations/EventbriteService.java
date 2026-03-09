package com.KKDixon.nexus.service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EventbriteService {

    @Value("${eventbrite.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://www.eventbriteapi.com/v3";

    public String fetchLocalEvents(String city) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = BASE_URL + "/events/search/" +
                    "?location.address=" + city.replace(" ", "%20") +
                    "&location.within=25mi" +
                    "&expand=venue" +
                    "&sort_by=date" +
                    "&page_size=10";

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch local events\"}";
        }
    }
}