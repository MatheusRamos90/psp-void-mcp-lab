package com.matheushrs.psp_void_tools_orch.client;

import tools.jackson.databind.JsonNode;
import com.matheushrs.psp_void_tools_orch.security.ServiceTokenHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Forwards tool execution requests to psp-void-orch.
 * Automatically includes the service-account JWT from {@link ServiceTokenHolder}.
 */
@Component
@RequiredArgsConstructor
public class OrchApiClient {

    @Qualifier("orchWebClient")
    private final WebClient webClient;

    private final ServiceTokenHolder tokenHolder;

    public JsonNode execute(String method, String path, JsonNode body, String origin) {
        WebClient.RequestBodySpec requestSpec = webClient
                .method(HttpMethod.valueOf(method))
                .uri(path)
                .header("Authorization", "Bearer " + tokenHolder.get())
                .header("X-Origin", origin != null ? origin : "MCP");

        WebClient.RequestHeadersSpec<?> headersSpec = (body != null && !body.isNull())
                ? requestSpec.bodyValue(body)
                : requestSpec;

        return headersSpec
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
