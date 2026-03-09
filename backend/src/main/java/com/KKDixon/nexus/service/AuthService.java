package com.KKDixon.nexus.service;

import com.KKDixon.nexus.dto.AuthResponse;
import com.KKDixon.nexus.dto.LoginRequest;
import com.KKDixon.nexus.dto.RegisterRequest;
import com.KKDixon.nexus.model.User;
import com.KKDixon.nexus.model.UserSettings;
import com.KKDixon.nexus.repository.UserRepository;
import com.KKDixon.nexus.repository.UserSettingsRepository;
import com.KKDixon.nexus.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Email already in use"
            );
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                "Username already taken"
            );
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        UserSettings settings = new UserSettings();
        settings.setUser(user);
        settings.setNewsTopics(new ArrayList<>());
        userSettingsRepository.save(settings);

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException(
                "Invalid email or password"
            );
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}