package com.matheushrs.psp_void_tools_orch.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Holds the service-account JWT obtained from psp-void-orch.
 * Parses the {@code exp} claim from the token payload so that
 * {@link TokenRefreshScheduler} can proactively refresh before expiry.
 */
@Slf4j
@Component
public class ServiceTokenHolder {

    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\":(\\d+)");

    private volatile String  token;
    private volatile Instant expiresAt;

    public void set(String token) {
        this.token     = token;
        this.expiresAt = parseExpiry(token);
        if (expiresAt != null) {
            log.info("Service JWT updated (expires at {}).", expiresAt);
        } else {
            log.info("Service JWT updated (expiry unknown).");
        }
    }

    public String get() {
        if (token == null) {
            throw new IllegalStateException("Service JWT not yet initialized — orch may not be ready.");
        }
        return token;
    }

    public boolean isInitialized() {
        return token != null;
    }

    /**
     * Returns true when the token has already expired or will expire
     * within the given {@code margin}.
     */
    public boolean needsRefresh(Duration margin) {
        if (expiresAt == null) return false;
        return Instant.now().isAfter(expiresAt.minus(margin));
    }

    /** Decodes the JWT payload (Base64URL) and extracts the {@code exp} claim. */
    private Instant parseExpiry(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            var matcher = EXP_PATTERN.matcher(payload);
            if (matcher.find()) {
                return Instant.ofEpochSecond(Long.parseLong(matcher.group(1)));
            }
        } catch (Exception e) {
            log.warn("Could not parse JWT expiry claim: {}", e.getMessage());
        }
        return null;
    }
}
