package com.KKDixon.nexus.service.integrations;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuoteService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://api.quotable.io";

    private static final String FALLBACK_QUOTE =
            "{\"content\": \"The best way to get started is to quit " +
            "talking and begin doing.\", \"author\": \"Walt Disney\"}";

    public String fetchQuoteOfTheDay() {
        try {
            String result = restTemplate.getForObject(
                    BASE_URL + "/random",
                    String.class
            );
            return result != null ? result : FALLBACK_QUOTE;
        } catch (Exception e) {
            return FALLBACK_QUOTE;
        }
    }
}