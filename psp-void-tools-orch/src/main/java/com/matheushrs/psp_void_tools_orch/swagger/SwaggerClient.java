package com.matheushrs.psp_void_tools_orch.swagger;

import com.matheushrs.psp_void_tools_orch.security.ServiceTokenHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fetches the raw OpenAPI JSON spec from psp-void-orch as a plain String.
 * The /v3/api-docs endpoint is public (no auth required), but we include
 * the token anyway in case the policy changes.
 */
@Component
public class SwaggerClient {

    private final WebClient orchWebClient;
    private final ServiceTokenHolder tokenHolder;

    public SwaggerClient(
            @Qualifier("orchWebClient") WebClient orchWebClient,
            ServiceTokenHolder tokenHolder) {
        this.orchWebClient = orchWebClient;
        this.tokenHolder = tokenHolder;
    }

    public String fetchSpec() {
        WebClient.RequestHeadersSpec<?> request = orchWebClient
                .get()
                .uri("/v3/api-docs");

        if (tokenHolder.isInitialized()) {
            request = orchWebClient
                    .get()
                    .uri("/v3/api-docs")
                    .header("Authorization", "Bearer " + tokenHolder.get());
        }

        return request.retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
