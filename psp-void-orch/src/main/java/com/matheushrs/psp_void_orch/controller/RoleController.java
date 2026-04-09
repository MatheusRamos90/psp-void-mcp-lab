package com.matheushrs.psp_void_orch.controller;

import com.matheushrs.psp_void_orch.client.CoreApiClient;
import com.matheushrs.psp_void_orch.dto.*;
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
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management — write operations require ADMIN authority")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final CoreApiClient coreClient;

    // ── Read — any authenticated user ──────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all roles")
    public List<RoleResponse> findAll() {
        return coreClient.findAllRoles();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public RoleResponse findById(@PathVariable UUID id) {
        return coreClient.findRoleById(id);
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "List roles assigned to a user")
    public List<UserRoleResponse> findByUserId(@PathVariable UUID userId) {
        return coreClient.findRolesByUserId(userId);
    }

    // ── Write — ADMIN only ─────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a new role  [ADMIN]")
    public ResponseEntity<RoleResponse> create(
            @RequestBody RoleRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coreClient.createRole(request, origin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a role  [ADMIN]")
    public RoleResponse update(
            @PathVariable UUID id,
            @RequestBody RoleRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return coreClient.updateRole(id, request, origin);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a role  [ADMIN]")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        coreClient.deleteRole(id, origin);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Assign a role to a user  [ADMIN]")
    public ResponseEntity<UserRoleResponse> assign(
            @RequestBody UserRoleRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coreClient.assignRole(request, origin));
    }

    @DeleteMapping("/revoke")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Revoke a role from a user  [ADMIN]")
    public ResponseEntity<Void> revoke(
            @RequestParam UUID userId,
            @RequestParam UUID roleId,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        coreClient.revokeRole(userId, roleId, origin);
        return ResponseEntity.noContent().build();
    }
}
