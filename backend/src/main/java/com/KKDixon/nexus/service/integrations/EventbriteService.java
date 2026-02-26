package com.KKDixon.nexus.service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EventbriteService {

    @Value("${eventbrite.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchLocalEvents(String city) {
        // TODO: implement Eventbrite API call
        return "{}";
    }
}