package com.matheushrs.psp_void_elasticsearch_db.service;

import com.matheushrs.psp_void_elasticsearch_db.document.LogDocument;
import com.matheushrs.psp_void_elasticsearch_db.dto.LogRequest;
import com.matheushrs.psp_void_elasticsearch_db.dto.LogResponse;
import com.matheushrs.psp_void_elasticsearch_db.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository repository;

    public LogResponse save(LogRequest request) {
        var doc = LogDocument.builder()
                .id(UUID.randomUUID().toString())
                .trace(request.trace())
                .origin(request.origin())
                .createdAt(Instant.now())
                .build();
        return toResponse(repository.save(doc));
    }

    public List<LogResponse> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::toResponse)
                .toList();
    }

    public List<LogResponse> findByOrigin(String origin) {
        return repository.findByOrigin(origin).stream().map(this::toResponse).toList();
    }

    private LogResponse toResponse(LogDocument doc) {
        return new LogResponse(doc.getId(), doc.getTrace(), doc.getOrigin(), doc.getCreatedAt());
    }
}
