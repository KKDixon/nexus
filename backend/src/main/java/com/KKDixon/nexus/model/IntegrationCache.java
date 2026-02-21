package com.KKDixon.nexus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "integration_cache",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "integration"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationCache {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String integration; // "github", "weather", "news", "spotify", "google"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String data;

    @CreationTimestamp
    private LocalDateTime fetchedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}