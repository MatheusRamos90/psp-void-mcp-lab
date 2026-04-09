package com.matheushrs.psp_void_core.controller;

import com.matheushrs.psp_void_core.dto.UserCredentialsRequest;
import com.matheushrs.psp_void_core.dto.UserRequest;
import com.matheushrs.psp_void_core.dto.UserResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import com.matheushrs.psp_void_core.service.UserService;
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
    public ResponseEntity<UserResponse> create(
            @RequestBody UserRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request, Origin.valueOf(origin)));
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
    public UserResponse update(
            @PathVariable UUID id,
            @RequestBody UserRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return service.update(id, request, Origin.valueOf(origin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        service.delete(id, Origin.valueOf(origin));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/authenticate")
    public UserResponse authenticate(@RequestBody UserCredentialsRequest request) {
        return service.authenticate(request);
    }
}
