package com.matheushrs.psp_void_tools_orch.security;

import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Handles authentication of the service account against psp-void-orch.
 * Extracted from ServiceAuthConfig to be reusable by both the startup
 * ApplicationRunner and the TokenRefreshScheduler.
 */
@Slf4j
@Component
public class ServiceAccountAuthenticator {

    private final WebClient orchWebClient;
    private final ServiceTokenHolder tokenHolder;
    private final ObjectMapper objectMapper;

    @Value("${service-account.email}")
    private String serviceEmail;

    @Value("${service-account.password}")
    private String servicePassword;

    public ServiceAccountAuthenticator(
            @Qualifier("orchWebClient") WebClient orchWebClient,
            ServiceTokenHolder tokenHolder,
            ObjectMapper objectMapper) {
        this.orchWebClient = orchWebClient;
        this.tokenHolder   = tokenHolder;
        this.objectMapper  = objectMapper;
    }

    /**
     * Authenticates and stores the new JWT in {@link ServiceTokenHolder}.
     * Throws a runtime exception if authentication fails.
     */
    public void authenticate() {
        log.info("Authenticating service account: {}", serviceEmail);
        try {
            String requestBody = objectMapper.writeValueAsString(
                    Map.of("email", serviceEmail, "password", servicePassword));

            String responseBody = orchWebClient
                    .post()
                    .uri("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String token = objectMapper.readTree(responseBody).path("token").asText();
            tokenHolder.set(token);

        } catch (Exception e) {
            throw new RuntimeException("Service account authentication failed: " + e.getMessage(), e);
        }
    }
}
