package com.KKDixon.nexus.service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NewsService {

    @Value("${news.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://newsapi.org/v2";

    public String fetchNews(List<String> topics) {
        String query = topics == null || topics.isEmpty()
                ? "technology"
                : String.join(" OR ", topics);

        String url = BASE_URL + "/everything" +
                "?q=" + query.replace(" ", "%20") +
                "&sortBy=publishedAt" +
                "&pageSize=10" +
                "&apiKey=" + apiKey;

        return restTemplate.getForObject(url, String.class);
    }
}