package com.matheushrs.psp_void_tools_orch.executor;

import tools.jackson.databind.JsonNode;
import com.matheushrs.psp_void_tools_orch.client.OrchApiClient;
import com.matheushrs.psp_void_tools_orch.registry.ToolRegistry;
import com.matheushrs.psp_void_tools_orch.tools.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resolves a tool by name from the registry, builds the resolved URL
 * (substituting path params and appending query params from the input JSON),
 * and delegates the HTTP call to {@link OrchApiClient}.
 * <p>
 * Input JSON convention:
 * <ul>
 *   <li>Path param "id"  → top-level field "id"</li>
 *   <li>Query param "userId" → top-level field "userId"</li>
 *   <li>Request body     → nested field "body"</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolExecutor {

    private final ToolRegistry registry;
    private final OrchApiClient orchClient;

    public JsonNode execute(String toolName, JsonNode input, String origin) {
        Tool tool = registry.findByName(toolName)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolName));

        // 1. Substitute path parameters  (/products/{id} → /products/abc-123)
        String resolvedPath = tool.getPath();
        for (String param : tool.getPathParams()) {
            JsonNode val = input != null ? input.get(param) : null;
            if (val != null) {
                resolvedPath = resolvedPath.replace("{" + param + "}", val.asText());
            }
        }

        // 2. Append query string  (?userId=x&roleId=y)
        if (!tool.getQueryParams().isEmpty() && input != null) {
            StringBuilder qs = new StringBuilder();
            for (String param : tool.getQueryParams()) {
                JsonNode val = input.get(param);
                if (val != null) {
                    qs.append(qs.isEmpty() ? "?" : "&")
                      .append(param).append("=").append(val.asText());
                }
            }
            resolvedPath += qs;
        }

        // 3. Extract request body from "body" field (POST / PUT / PATCH)
        JsonNode body = (input != null && input.has("body")) ? input.get("body") : null;

        log.info("Executing tool '{}': {} {}", toolName, tool.getMethod(), resolvedPath);
        return orchClient.execute(tool.getMethod(), resolvedPath, body, origin);
    }
}
