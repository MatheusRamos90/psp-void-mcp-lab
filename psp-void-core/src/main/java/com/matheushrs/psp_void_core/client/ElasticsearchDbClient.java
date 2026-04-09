package com.matheushrs.psp_void_core.client;

import com.matheushrs.psp_void_core.dto.LogResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ElasticsearchDbClient {

    @Qualifier("elasticsearchDbWebClient")
    private final WebClient webClient;

    public void saveLog(String trace, Origin origin) {
        webClient.post().uri("/logs")
                .bodyValue(Map.of("trace", trace, "origin", origin.name()))
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }

    public List<LogResponse> findAllLogs() {
        return webClient.get().uri("/logs")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<LogResponse>>() {})
                .block();
    }
}
