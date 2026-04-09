package com.matheushrs.psp_void_tools_orch.controller;

import tools.jackson.databind.JsonNode;
import com.matheushrs.psp_void_tools_orch.executor.ToolExecutor;
import com.matheushrs.psp_void_tools_orch.registry.ToolRegistry;
import com.matheushrs.psp_void_tools_orch.swagger.SwaggerToolLoader;
import com.matheushrs.psp_void_tools_orch.tools.Tool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
@Tag(name = "Tools", description = "Dynamic tool registry and execution — AI agent entrypoint")
public class ToolController {

    private final ToolRegistry registry;
    private final ToolExecutor executor;
    private final SwaggerToolLoader loader;

    @GetMapping
    @Operation(summary = "List all registered tools")
    public List<Tool> listTools() {
        return registry.findAll();
    }

    @PostMapping("/reload")
    @Operation(summary = "Reload tools from psp-void-orch OpenAPI spec")
    public ResponseEntity<String> reloadTools() {
        var tools = loader.load();
        registry.registerAll(tools);
        return ResponseEntity.ok("Loaded " + tools.size() + " tools");
    }

    @PostMapping("/{toolName}/execute")
    @Operation(summary = "Execute a tool by name")
    public ResponseEntity<JsonNode> execute(
            @PathVariable String toolName,
            @RequestBody(required = false) JsonNode body,
            @RequestHeader(value = "X-Origin", defaultValue = "MCP") String origin) {
        JsonNode result = executor.execute(toolName, body, origin);
        return ResponseEntity.ok(result);
    }
}
