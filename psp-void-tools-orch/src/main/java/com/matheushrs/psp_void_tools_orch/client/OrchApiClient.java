package com.matheushrs.psp_void_tools_orch.client;

import tools.jackson.databind.JsonNode;
import com.matheushrs.psp_void_tools_orch.security.ServiceAccountAuthenticator;
import com.matheushrs.psp_void_tools_orch.security.ServiceTokenHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Forwards tool execution requests to psp-void-orch.
 * Automatically includes the service-account JWT from {@link ServiceTokenHolder}.
 *
 * If psp-void-orch returns 401 (token expired between the scheduled refresh
 * window), the client refreshes the token once and retries the request.
 */
@Slf4j
@Component
public class OrchApiClient {

    @Qualifier("orchWebClient")
    private final WebClient webClient;

    private final ServiceTokenHolder          tokenHolder;
    private final ServiceAccountAuthenticator authenticator;

    public OrchApiClient(
            @Qualifier("orchWebClient") WebClient webClient,
            ServiceTokenHolder tokenHolder,
            ServiceAccountAuthenticator authenticator) {
        this.webClient     = webClient;
        this.tokenHolder   = tokenHolder;
        this.authenticator = authenticator;
    }

    public JsonNode execute(String method, String path, JsonNode body, String origin) {
        try {
            return doExecute(method, path, body, origin);
        } catch (WebClientResponseException.Unauthorized e) {
            log.warn("Received 401 from orch — JWT expired, refreshing and retrying once...");
            authenticator.authenticate();
            return doExecute(method, path, body, origin);
        }
    }

    private JsonNode doExecute(String method, String path, JsonNode body, String origin) {
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
