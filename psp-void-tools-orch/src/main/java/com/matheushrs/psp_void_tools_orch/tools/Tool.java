package com.matheushrs.psp_void_tools_orch.tools;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Represents a single executable tool derived from a psp-void-orch OpenAPI endpoint.
 * <p>
 * name         → unique identifier (e.g. "post_products")
 * method       → HTTP verb (GET, POST, PUT, DELETE)
 * path         → raw path with placeholders (e.g. "/products/{id}")
 * description  → human-readable summary from the OpenAPI spec
 * inputSchema  → JSON Schema string describing inputs (used by MCP protocol)
 * pathParams   → ordered list of path parameter names (e.g. ["id"])
 * queryParams  → ordered list of query parameter names (e.g. ["userId", "roleId"])
 */
@Value
@Builder
public class Tool {
    String name;
    String method;
    String path;
    String description;

    @Builder.Default
    String inputSchema = "{\"type\":\"object\"}";

    @Builder.Default
    List<String> pathParams = List.of();

    @Builder.Default
    List<String> queryParams = List.of();
}
