package com.matheushrs.psp_void_tools_orch.mcp;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.matheushrs.psp_void_tools_orch.executor.ToolExecutor;
import com.matheushrs.psp_void_tools_orch.registry.ToolRegistry;
import com.matheushrs.psp_void_tools_orch.tools.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Component;

/**
 * Bridges the dynamic {@link ToolRegistry} to the Spring AI MCP Server protocol.
 * <p>
 * Spring AI's {@code spring-ai-starter-mcp-server-webmvc} scans all
 * {@link ToolCallbackProvider} beans and exposes their tools via:
 * <ul>
 *   <li>{@code GET  /sse}          — SSE connection endpoint (Cursor connects here)</li>
 *   <li>{@code POST /mcp/message}  — JSON-RPC 2.0 message endpoint</li>
 * </ul>
 * <p>
 * {@code getToolCallbacks()} is called dynamically on each {@code tools/list} request,
 * so tools loaded after startup (via {@code ApplicationRunner}) are always visible.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicToolCallbackProvider implements ToolCallbackProvider {

    private final ToolRegistry registry;
    private final ToolExecutor executor;
    private final ObjectMapper objectMapper;

    @Override
    public ToolCallback[] getToolCallbacks() {
        return registry.findAll().stream()
                .map(this::toCallback)
                .toArray(ToolCallback[]::new);
    }

    private ToolCallback toCallback(Tool tool) {
        ToolDefinition definition = ToolDefinition.builder()
                .name(tool.getName())
                .description(tool.getDescription())
                .inputSchema(tool.getInputSchema())
                .build();

        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return definition;
            }

            @Override
            public String call(String toolInput) {
                try {
                    String json = (toolInput == null || toolInput.isBlank()) ? "{}" : toolInput;
                    JsonNode input  = objectMapper.readTree(json);
                    JsonNode result = executor.execute(tool.getName(), input, "MCP");
                    return result != null ? objectMapper.writeValueAsString(result) : "{}";
                } catch (Exception e) {
                    log.warn("Tool '{}' execution failed: {}", tool.getName(), e.getMessage());
                    return "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}";
                }
            }
        };
    }
}
