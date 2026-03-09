package com.KKDixon.nexus.service.integrations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5";

    public String fetchWeather(String city) {
        String url = BASE_URL + "/weather" +
                "?q=" + city +
                "&appid=" + apiKey +
                "&units=imperial";

        return restTemplate.getForObject(url, String.class);
    }

    public String fetchForecast(String city) {
        String url = BASE_URL + "/forecast" +
                "?q=" + city +
                "&appid=" + apiKey +
                "&units=imperial" +
                "&cnt=8";

        return restTemplate.getForObject(url, String.class);
    }
}