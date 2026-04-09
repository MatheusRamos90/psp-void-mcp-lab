package com.matheushrs.psp_void_tools_orch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.orch.url}")
    private String orchUrl;

    @Bean("orchWebClient")
    public WebClient orchWebClient() {
        return WebClient.builder()
                .baseUrl(orchUrl)
                .build();
    }
}
