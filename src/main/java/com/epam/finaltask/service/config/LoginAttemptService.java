package com.epam.finaltask.service.config;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final long LOCK_TIME_DURATION = 2 * 60 * 1000;

    private final ConcurrentHashMap<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public void loginFailed(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null) {
            attempt = new LoginAttempt(0, 0);
        }
        int attempts = attempt.attempts + 1;
        long lockUntil = 0;
        if (attempts >= MAX_ATTEMPT) {
            lockUntil = Instant.now().toEpochMilli() + LOCK_TIME_DURATION;
        }
        attemptsCache.put(username, new LoginAttempt(attempts, lockUntil));
    }

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null) return false;

        if (attempt.lockUntil > Instant.now().toEpochMilli()) {
            return true;
        }
        if (attempt.lockUntil != 0) {
            attemptsCache.remove(username);
            return false;
        }
        return false;
    }

    private static class LoginAttempt {
        final int attempts;
        final long lockUntil;

        LoginAttempt(int attempts, long lockUntil) {
            this.attempts = attempts;
            this.lockUntil = lockUntil;
        }
    }
}
