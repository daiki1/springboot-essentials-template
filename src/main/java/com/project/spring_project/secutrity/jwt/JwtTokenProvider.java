package com.project.spring_project.secutrity.jwt;

import com.project.spring_project.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    @Value("${app.jwtIssuer}")
    private String jwtIssuer;

    @Value("${app.jwtAudience}")
    private String jwtAudience;

    @Value("${app.jwtRefreshExpirationMs}")
    private long refreshExpirationInMs;

    @Getter
    private SecretKey key;

    @PostConstruct
    public void init() {
        if (jwtSecret == null) {
            throw new IllegalArgumentException("JWT secret key is not defined");
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Method to extract claims from the token
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Use the SecretKey here
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setIssuer(jwtIssuer)          // Add issuer
                .setAudience(jwtAudience)      // Add audience
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

        return generateToken(authentication); // delegate to your existing method
    }

    // Method to get the expiration date of the token
    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !isTokenExpired(token)
                    && claims.getIssuer().equals(jwtIssuer)
                    && claims.getAudience().equals(jwtAudience);
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException |
                 ExpiredJwtException |
                 SignatureException ex) {
            return false;
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // Method to log the claims of the token
    // This is just for demonstration purposes
    public void logTokenClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Audience: " + claims.getAudience());
        System.out.println("Issued At: " + claims.getIssuedAt());
        System.out.println("Expiration: " + claims.getExpiration());
    }

    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationInMs); // define this in config

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(jwtIssuer)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}