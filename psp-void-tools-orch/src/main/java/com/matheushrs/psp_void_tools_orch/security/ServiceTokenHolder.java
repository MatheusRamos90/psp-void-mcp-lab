package com.matheushrs.psp_void_tools_orch.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Holds the service-account JWT obtained from psp-void-orch on startup.
 * All outgoing requests to psp-void-orch use this token.
 */
@Slf4j
@Component
public class ServiceTokenHolder {

    private volatile String token;

    public void set(String token) {
        this.token = token;
        log.info("Service JWT updated.");
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
}
