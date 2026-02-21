package com.KKDixon.nexus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String githubUsername;

    private String weatherCity;

    @ElementCollection
    @CollectionTable(
        name = "user_news_topics",
        joinColumns = @JoinColumn(name = "settings_id")
    )
    @Column(name = "topic")
    private List<String> newsTopics;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}