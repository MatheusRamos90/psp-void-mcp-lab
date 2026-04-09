package com.matheushrs.psp_void_postgres_db.controller;

import com.matheushrs.psp_void_postgres_db.dto.RoleRequest;
import com.matheushrs.psp_void_postgres_db.dto.RoleResponse;
import com.matheushrs.psp_void_postgres_db.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<RoleResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public RoleResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable UUID id, @RequestBody RoleRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
