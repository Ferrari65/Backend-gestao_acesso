package com.services.auth.util;

import com.repositories.auth.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenCleaner {

    private final PasswordResetTokenRepository tokenRepo;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        var now = Instant.now();
        int deleted = tokenRepo.deleteByExpiresAtBeforeOrUsedTrue(now);
        if (deleted > 0) {
            System.out.println("🧹 Tokens inválidos removidos: " + deleted);
        }
    }
}
