package com.project.spring_project.secutrity.jwt;

import com.project.spring_project.entity.user.User;
import com.project.spring_project.secutrity.services.CustomUserDetails;
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

    /**
     * Extracts claims from the provided JWT token.
     * <p>
     * This method uses the SecretKey to parse the token and retrieve its claims.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Use the SecretKey here
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates a JWT token for the given authentication.
     * <p>
     * This method creates a token with the username, roles, user ID, and email as claims.
     *
     * @param authentication the authentication object
     * @return the generated JWT token
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationInMs);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setIssuer(jwtIssuer)          // Add issuer
                .setAudience(jwtAudience)      // Add audience
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token for the given user.
     * <p>
     * This method creates a token with the username and roles as claims.
     *
     * @param user the user object
     * @return the generated JWT token
     */
    public String generateToken(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

        return generateToken(authentication); // delegate to your existing method
    }

    /**
     * Retrieves the expiration date from the provided JWT token.
     * <p>
     * This method uses the SecretKey to parse the token and retrieve its expiration date.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    /**
     * Checks if the provided JWT token is expired.
     * <p>
     * This method compares the expiration date of the token with the current date.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    /**
     * Validates the provided JWT token.
     * <p>
     * This method checks if the token is expired and if the issuer and audience match the expected values.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
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

    /**
     * Retrieves the username from the provided JWT token.
     * <p>
     * This method uses the SecretKey to parse the token and retrieve the username.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /**
     * Logs the claims of the provided JWT token.
     * <p>
     * This method uses the SecretKey to parse the token and retrieve its claims.
     *
     * This is just for demonstration purposes
     *
     * @param token the JWT token
     */
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

    /**
     * Generates a refresh token for the given authentication.
     * <p>
     * This method creates a token with the username as the subject and sets an expiration date.
     *
     * @param authentication the authentication object
     * @return the generated refresh token
     */
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