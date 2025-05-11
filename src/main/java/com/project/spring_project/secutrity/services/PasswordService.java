package com.project.spring_project.secutrity.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    @Value("${security.pepper}")
    private String pepper;

    /**
     * Encodes a raw password using the password encoder and a pepper.
     *
     * @param rawPassword the raw password to encode
     * @return the encoded password
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword + pepper);
    }

    /**
     * Compares a raw password with an encoded password.
     *
     * @param rawPassword the raw password to compare
     * @param encodedPassword the encoded password to compare against
     * @return true if the passwords match, false otherwise
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword + pepper, encodedPassword);
    }
}