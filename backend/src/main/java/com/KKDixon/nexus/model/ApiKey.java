package com.KKDixon.nexus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String keyHash;

    @Column(nullable = false, length = 10)
    private String keyPrefix;

    private String name;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime lastUsed;

    @CreationTimestamp
    private LocalDateTime createdAt;
}