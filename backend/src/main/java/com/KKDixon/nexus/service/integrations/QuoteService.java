package com.KKDixon.nexus.service.integrations;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuoteService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchQuoteOfTheDay() {
        // TODO: implement quotable.io API call
        return "{}";
    }
}