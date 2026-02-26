package com.KKDixon.nexus.service.integrations;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchGithubData(String username) {
        // TODO: implement GitHub API call
        return "{}";
    }
}