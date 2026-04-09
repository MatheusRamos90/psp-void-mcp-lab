package com.matheushrs.psp_void_orch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Propagates HTTP errors received from psp-void-core back to the caller
 * with the original status code and body.
 *
 * Without this handler, WebClientResponseException would bubble up as a 500.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleDownstreamError(WebClientResponseException ex) {
        log.warn("Downstream error {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        return ResponseEntity
                .status(ex.getStatusCode())
                .headers(h -> h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .body(ex.getResponseBodyAsString());
    }
}
