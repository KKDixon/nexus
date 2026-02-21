package com.KKDixon.nexus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String integration; // "github", "weather", "news", "spotify", "google"

    @Column(nullable = false)
    private String status; // "success", "failed", "skipped"

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Integer durationMs;

    @CreationTimestamp
    private LocalDateTime createdAt;
}