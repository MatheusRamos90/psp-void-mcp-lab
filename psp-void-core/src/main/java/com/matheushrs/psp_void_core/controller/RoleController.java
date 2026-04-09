package com.matheushrs.psp_void_core.controller;

import com.matheushrs.psp_void_core.dto.*;
import com.matheushrs.psp_void_core.enums.Origin;
import com.matheushrs.psp_void_core.service.RoleService;
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
    public ResponseEntity<RoleResponse> create(
            @RequestBody RoleRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request, Origin.valueOf(origin)));
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
    public RoleResponse update(
            @PathVariable UUID id,
            @RequestBody RoleRequest request,
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

    @PostMapping("/assign")
    public ResponseEntity<UserRoleResponse> assign(
            @RequestBody UserRoleRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.assignRoleToUser(request, Origin.valueOf(origin)));
    }

    @GetMapping("/by-user/{userId}")
    public List<UserRoleResponse> findByUserId(@PathVariable UUID userId) {
        return service.findRolesByUserId(userId);
    }

    @GetMapping("/names-by-user/{userId}")
    public List<String> findRoleNamesByUserId(@PathVariable UUID userId) {
        return service.findRoleNamesByUserId(userId);
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<Void> revoke(
            @RequestParam UUID userId,
            @RequestParam UUID roleId,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        service.revokeRole(userId, roleId, Origin.valueOf(origin));
        return ResponseEntity.noContent().build();
    }
}
