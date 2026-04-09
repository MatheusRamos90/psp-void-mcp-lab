package com.matheushrs.psp_void_tools_orch.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Proactively refreshes the service-account JWT before it expires.
 *
 * Runs every {@code service-account.token-check-interval-ms} milliseconds
 * (default: 60 s) and triggers a re-authentication when fewer than
 * {@code service-account.token-refresh-margin-seconds} seconds remain
 * (default: 300 s = 5 min).
 *
 * An {@link AtomicBoolean} guard ensures only one refresh runs at a time,
 * even if the scheduler fires while a previous attempt is still in progress.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRefreshScheduler {

    private final ServiceAccountAuthenticator authenticator;
    private final ServiceTokenHolder          tokenHolder;

    @Value("${service-account.token-refresh-margin-seconds:300}")
    private int refreshMarginSeconds;

    private final AtomicBoolean refreshing = new AtomicBoolean(false);

    @Scheduled(fixedDelayString = "${service-account.token-check-interval-ms:60000}")
    public void checkAndRefresh() {
        if (!tokenHolder.isInitialized()) {
            return;
        }
        if (!tokenHolder.needsRefresh(Duration.ofSeconds(refreshMarginSeconds))) {
            return;
        }
        if (!refreshing.compareAndSet(false, true)) {
            log.debug("Token refresh already in progress, skipping.");
            return;
        }
        try {
            log.info("JWT approaching expiry — refreshing service account token...");
            authenticator.authenticate();
            log.info("JWT refreshed successfully.");
        } catch (Exception e) {
            log.error("Scheduled JWT refresh failed: {}", e.getMessage());
        } finally {
            refreshing.set(false);
        }
    }
}
