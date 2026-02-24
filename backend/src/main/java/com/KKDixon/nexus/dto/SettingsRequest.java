package com.KKDixon.nexus.dto;

import lombok.Data;
import java.util.List;

@Data
public class SettingsRequest {
    private String githubUsername;
    private String weatherCity;
    private List<String> newsTopics;
}