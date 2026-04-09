package com.matheushrs.psp_void_tools_orch.config;

import io.modelcontextprotocol.server.McpAsyncServer;
import com.matheushrs.psp_void_tools_orch.mcp.DynamicToolCallbackProvider;
import com.matheushrs.psp_void_tools_orch.registry.ToolRegistry;
import com.matheushrs.psp_void_tools_orch.security.ServiceAccountAuthenticator;
import com.matheushrs.psp_void_tools_orch.swagger.SwaggerToolLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * On startup:
 * 1. Authenticates the service account via {@link ServiceAccountAuthenticator}
 * 2. Loads tools from the OpenAPI spec and registers them in the MCP server
 *
 * Token renewal is handled independently by {@link com.matheushrs.psp_void_tools_orch.security.TokenRefreshScheduler}.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ServiceAuthConfig {

    private final ServiceAccountAuthenticator authenticator;
    private final SwaggerToolLoader           loader;
    private final ToolRegistry                registry;
    private final DynamicToolCallbackProvider toolCallbackProvider;
    private final McpAsyncServer              mcpAsyncServer;

    @Bean
    public ApplicationRunner authenticateAndLoadTools() {
        return args -> {
            try {
                authenticator.authenticate();

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
