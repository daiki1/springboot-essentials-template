package com.project.spring_project.secutrity.services;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Resolves a bucket for the given key. If the bucket does not exist, it creates a new one.
     *
     * @param key The key for which to resolve the bucket.
     * @return The resolved bucket.
     */
    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::newBucket);
    }

    /**
     * Attempts to consume a token from the bucket associated with the given key.
     *
     * @param key The key for which to consume a token.
     * @return true if the token was successfully consumed, false otherwise.
     */
    public boolean tryConsume(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, this::newBucket);
        return bucket.tryConsume(1);
    }

    /**
     * Creates a new bucket with a specific rate limit.
     *
     * @param key The key for which to create the bucket.
     * @return A new bucket with a rate limit of 5 tokens per minute.
     */
    private Bucket newBucket(String key) {
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    public void resetAll() {
        buckets.clear();
    }
}