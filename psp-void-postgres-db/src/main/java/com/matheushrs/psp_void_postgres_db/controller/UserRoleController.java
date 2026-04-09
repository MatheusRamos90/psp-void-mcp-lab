package com.matheushrs.psp_void_postgres_db.controller;

import com.matheushrs.psp_void_postgres_db.dto.UserRoleRequest;
import com.matheushrs.psp_void_postgres_db.dto.UserRoleResponse;
import com.matheushrs.psp_void_postgres_db.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService service;

    @PostMapping
    public ResponseEntity<UserRoleResponse> assign(@RequestBody UserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.assign(request));
    }

    @GetMapping("/by-user/{userId}")
    public List<UserRoleResponse> findByUserId(@PathVariable UUID userId) {
        return service.findByUserId(userId);
    }

    @GetMapping("/by-role/{roleId}")
    public List<UserRoleResponse> findByRoleId(@PathVariable UUID roleId) {
        return service.findByRoleId(roleId);
    }

    @DeleteMapping
    public ResponseEntity<Void> revoke(@RequestParam UUID userId, @RequestParam UUID roleId) {
        service.revoke(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/names-by-user/{userId}")
    public List<String> findRoleNamesByUserId(@PathVariable UUID userId) {
        return service.findRoleNamesByUserId(userId);
    }
}
