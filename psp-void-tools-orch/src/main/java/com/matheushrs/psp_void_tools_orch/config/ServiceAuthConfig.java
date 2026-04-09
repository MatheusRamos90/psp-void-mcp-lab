package com.matheushrs.psp_void_tools_orch.config;

import io.modelcontextprotocol.server.McpAsyncServer;
import tools.jackson.databind.ObjectMapper;
import com.matheushrs.psp_void_tools_orch.mcp.DynamicToolCallbackProvider;
import com.matheushrs.psp_void_tools_orch.registry.ToolRegistry;
import com.matheushrs.psp_void_tools_orch.security.ServiceTokenHolder;
import com.matheushrs.psp_void_tools_orch.swagger.SwaggerToolLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

/**
 * On startup:
 * 1. Authenticates the service account against psp-void-orch POST /auth/login
 * 2. Stores the JWT in ServiceTokenHolder
 * 3. Loads tools from the OpenAPI spec and registers them in the MCP server
 *
 * Uses raw String body + String response to avoid WebClient codec configuration issues.
 * Tools are registered dynamically via McpAsyncServer.addTool() because the
 * ToolCallbackConverterAutoConfiguration reads the provider at startup time (before
 * the ApplicationRunner runs), so the initial tool list is always empty.
 */
@Slf4j
@Configuration
public class ServiceAuthConfig {

    private final WebClient orchWebClient;
    private final ServiceTokenHolder tokenHolder;
    private final SwaggerToolLoader loader;
    private final ToolRegistry registry;
    private final ObjectMapper objectMapper;
    private final DynamicToolCallbackProvider toolCallbackProvider;
    private final McpAsyncServer mcpAsyncServer;

    @Value("${service-account.email}")
    private String serviceEmail;

    @Value("${service-account.password}")
    private String servicePassword;

    public ServiceAuthConfig(
            @Qualifier("orchWebClient") WebClient orchWebClient,
            ServiceTokenHolder tokenHolder,
            SwaggerToolLoader loader,
            ToolRegistry registry,
            ObjectMapper objectMapper,
            DynamicToolCallbackProvider toolCallbackProvider,
            McpAsyncServer mcpAsyncServer) {
        this.orchWebClient = orchWebClient;
        this.tokenHolder = tokenHolder;
        this.loader = loader;
        this.registry = registry;
        this.objectMapper = objectMapper;
        this.toolCallbackProvider = toolCallbackProvider;
        this.mcpAsyncServer = mcpAsyncServer;
    }

    @Bean
    public ApplicationRunner authenticateAndLoadTools() {
        return args -> {
            log.info("Authenticating service account: {}", serviceEmail);
            try {
                String requestBody = objectMapper.writeValueAsString(
                        new java.util.LinkedHashMap<String, String>() {{
                            put("email", serviceEmail);
                            put("password", servicePassword);
                        }}
                );

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

                log.info("Service account authenticated. Loading tools...");
                var tools = loader.load();
                registry.registerAll(tools);

                var callbacks = Arrays.asList(toolCallbackProvider.getToolCallbacks());
                McpToolUtils.toAsyncToolSpecifications(callbacks)
                        .forEach(spec -> mcpAsyncServer.addTool(spec).block());

                log.info("Ready. {} tools loaded and registered in MCP server.", tools.size());

            } catch (Exception e) {
                log.warn("Startup auth/tool-load failed (orch may not be ready yet): {}", e.getMessage());
            }
        };
    }
}
