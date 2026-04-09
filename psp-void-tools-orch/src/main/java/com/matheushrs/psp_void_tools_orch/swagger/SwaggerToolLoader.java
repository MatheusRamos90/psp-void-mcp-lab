package com.matheushrs.psp_void_tools_orch.swagger;

import com.matheushrs.psp_void_tools_orch.tools.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;

import java.util.*;

/**
 * Parses the OpenAPI spec and converts each endpoint into a {@link Tool}.
 * <p>
 * Tool naming convention: {method}_{path_segments}
 * e.g.  POST /products  →  post_products
 *       GET  /products/{id}  →  get_products_id
 * <p>
 * The generated JSON Schema for each tool includes:
 * - Path parameters (e.g. "id") as top-level string fields
 * - Query parameters as top-level fields
 * - Request body as a nested "body" object field
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SwaggerToolLoader {

    private final SwaggerClient swaggerClient;
    private final ObjectMapper objectMapper;

    private static final Set<String> HTTP_METHODS =
            Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

    @SuppressWarnings("unchecked")
    public List<Tool> load() {
        String specJson = swaggerClient.fetchSpec();
        List<Tool> tools = new ArrayList<>();

        Map<String, Object> spec;
        try {
            spec = objectMapper.readValue(specJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Failed to parse OpenAPI spec: {}", e.getMessage());
            return tools;
        }

        Map<String, Object> paths = (Map<String, Object>) spec.getOrDefault("paths", Map.of());

        for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
            String path = pathEntry.getKey();
            Map<String, Object> methods = (Map<String, Object>) pathEntry.getValue();

            for (Map.Entry<String, Object> methodEntry : methods.entrySet()) {
                String httpMethod = methodEntry.getKey().toUpperCase();
                if (!HTTP_METHODS.contains(httpMethod)) continue;

                Map<String, Object> operation = (Map<String, Object>) methodEntry.getValue();
                String summary = (String) operation.getOrDefault("summary", "No description");
                String toolName = buildToolName(httpMethod, path);

                List<String> pathParams  = new ArrayList<>();
                List<String> queryParams = new ArrayList<>();
                Map<String, Object> properties = new LinkedHashMap<>();
                List<String> required = new ArrayList<>();

                // ── parameters (path + query) ─────────────────────────────────
                List<Map<String, Object>> parameters =
                        (List<Map<String, Object>>) operation.getOrDefault("parameters", List.of());

                for (Map<String, Object> param : parameters) {
                    String pName = (String) param.getOrDefault("name", "");
                    String pIn   = (String) param.getOrDefault("in", "");
                    boolean pReq = Boolean.TRUE.equals(param.get("required"));
                    Map<String, Object> schema = (Map<String, Object>) param.getOrDefault("schema", Map.of());
                    String pType = (String) schema.getOrDefault("type", "string");
                    String pDesc = (String) param.getOrDefault("description", pIn + " param: " + pName);

                    Map<String, String> propDef = Map.of("type", pType, "description", pDesc);

                    if ("path".equals(pIn)) {
                        pathParams.add(pName);
                        properties.put(pName, propDef);
                        if (pReq) required.add(pName);
                    } else if ("query".equals(pIn)) {
                        queryParams.add(pName);
                        properties.put(pName, propDef);
                        if (pReq) required.add(pName);
                    }
                }

                // ── requestBody ───────────────────────────────────────────────
                Map<String, Object> requestBody =
                        (Map<String, Object>) operation.getOrDefault("requestBody", null);

                if (requestBody != null) {
                    properties.put("body", Map.of(
                            "type", "object",
                            "description", "JSON request body"));
                    if (Boolean.TRUE.equals(requestBody.get("required"))) {
                        required.add("body");
                    }
                }

                // ── build JSON Schema ─────────────────────────────────────────
                Map<String, Object> jsonSchema = new LinkedHashMap<>();
                jsonSchema.put("type", "object");
                jsonSchema.put("properties", properties);
                if (!required.isEmpty()) jsonSchema.put("required", required);

                String inputSchema;
                try {
                    inputSchema = objectMapper.writeValueAsString(jsonSchema);
                } catch (Exception e) {
                    inputSchema = "{\"type\":\"object\"}";
                }

                Tool tool = Tool.builder()
                        .name(toolName)
                        .method(httpMethod)
                        .path(path)
                        .description(summary)
                        .inputSchema(inputSchema)
                        .pathParams(List.copyOf(pathParams))
                        .queryParams(List.copyOf(queryParams))
                        .build();

                tools.add(tool);
                log.info("Loaded tool: {} {} — {}", httpMethod, path, summary);
            }
        }

        log.info("Total tools loaded: {}", tools.size());
        return tools;
    }

    private String buildToolName(String method, String path) {
        String sanitized = path
                .replaceAll("\\{[^}]+}", "id")
                .replaceAll("[^a-zA-Z0-9]+", "_")
                .replaceAll("^_|_$", "");
        return method.toLowerCase() + "_" + sanitized;
    }
}
