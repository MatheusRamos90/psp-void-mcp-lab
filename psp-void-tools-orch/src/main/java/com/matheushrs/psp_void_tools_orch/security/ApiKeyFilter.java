package com.matheushrs.psp_void_tools_orch.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Validates the {@code X-Api-Key} header on every incoming request.
 * <p>
 * Exempt paths: {@code /actuator/health} (used by Docker/infra health probes).
 * All other paths — including {@code /sse}, {@code /mcp/message}, and {@code /tools/*}
 * — require a valid key. Configure the expected value via {@code MCP_API_KEY} env var.
 */
@Slf4j
@Component
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-Api-Key";

    @Value("${mcp.api-key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/actuator/health")) {
            chain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader(API_KEY_HEADER);

        if (providedKey == null || !expectedApiKey.equals(providedKey)) {
            log.warn("Rejected request to {} — missing or invalid {}", path, API_KEY_HEADER);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing or invalid API key\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
