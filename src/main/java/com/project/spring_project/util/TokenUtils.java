package com.project.spring_project.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenUtils {

    /**
     * Hashes a given token using SHA-256 algorithm.
     *
     * @param token the token to be hashed
     * @return the hashed token as a hexadecimal string
     */
    public static String hashedToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    public static String generateRandomCode(int i) {
        StringBuilder code = new StringBuilder();
        for (int j = 0; j < i; j++) {
            int digit = (int) (Math.random() * 10); // Generate a random digit from 0 to 9
            code.append(digit);
        }
        return code.toString();
    }
}