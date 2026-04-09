package com.matheushrs.psp_void_orch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.core.url}")
    private String coreUrl;

    @Bean("coreWebClient")
    public WebClient coreWebClient() {
        return WebClient.builder().baseUrl(coreUrl).build();
    }
}
