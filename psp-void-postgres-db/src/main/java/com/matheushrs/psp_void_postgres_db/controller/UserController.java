package com.matheushrs.psp_void_postgres_db.controller;

import com.matheushrs.psp_void_postgres_db.dto.UserCredentialsRequest;
import com.matheushrs.psp_void_postgres_db.dto.UserRequest;
import com.matheushrs.psp_void_postgres_db.dto.UserResponse;
import com.matheushrs.psp_void_postgres_db.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @RequestBody UserRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/authenticate")
    public UserResponse authenticate(@RequestBody UserCredentialsRequest request) {
        return service.authenticate(request);
    }
}
