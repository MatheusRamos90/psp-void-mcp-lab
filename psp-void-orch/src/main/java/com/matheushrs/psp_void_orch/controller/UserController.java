package com.matheushrs.psp_void_orch.controller;

import com.matheushrs.psp_void_orch.client.CoreApiClient;
import com.matheushrs.psp_void_orch.dto.UserRequest;
import com.matheushrs.psp_void_orch.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management — write operations require ADMIN authority")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final CoreApiClient coreClient;

    // ── Read — ADMIN only (user data is sensitive) ─────────────────────────

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "List all users  [ADMIN]")
    public List<UserResponse> findAll() {
        return coreClient.findAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get user by ID  [ADMIN]")
    public UserResponse findById(@PathVariable UUID id) {
        return coreClient.findUserById(id);
    }

    // ── Write — ADMIN only ─────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a new user  [ADMIN]")
    public ResponseEntity<UserResponse> create(
            @RequestBody UserRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coreClient.createUser(request, origin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a user  [ADMIN]")
    public UserResponse update(
            @PathVariable UUID id,
            @RequestBody UserRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return coreClient.updateUser(id, request, origin);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a user  [ADMIN]")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        coreClient.deleteUser(id, origin);
        return ResponseEntity.noContent().build();
    }
}
