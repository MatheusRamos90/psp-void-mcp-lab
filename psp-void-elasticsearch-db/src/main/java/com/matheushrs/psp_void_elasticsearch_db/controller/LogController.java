package com.matheushrs.psp_void_elasticsearch_db.controller;

import com.matheushrs.psp_void_elasticsearch_db.dto.LogRequest;
import com.matheushrs.psp_void_elasticsearch_db.dto.LogResponse;
import com.matheushrs.psp_void_elasticsearch_db.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService service;

    @PostMapping
    public ResponseEntity<LogResponse> save(@RequestBody LogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @GetMapping
    public List<LogResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/by-origin/{origin}")
    public List<LogResponse> findByOrigin(@PathVariable String origin) {
        return service.findByOrigin(origin);
    }
}
