package com.matheushrs.psp_void_core.service;

import com.matheushrs.psp_void_core.client.ElasticsearchDbClient;
import com.matheushrs.psp_void_core.client.PostgresDbClient;
import com.matheushrs.psp_void_core.dto.RoleRequest;
import com.matheushrs.psp_void_core.dto.RoleResponse;
import com.matheushrs.psp_void_core.dto.UserRoleRequest;
import com.matheushrs.psp_void_core.dto.UserRoleResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final PostgresDbClient postgresDb;
    private final ElasticsearchDbClient elasticsearchDb;

    public RoleResponse create(RoleRequest request, Origin origin) {
        var role = postgresDb.createRole(request);
        elasticsearchDb.saveLog("role created: " + role.name(), origin);
        return role;
    }

    public List<RoleResponse> findAll() {
        return postgresDb.findAllRoles();
    }

    public RoleResponse findById(UUID id) {
        return postgresDb.findRoleById(id);
    }

    public RoleResponse update(UUID id, RoleRequest request, Origin origin) {
        var role = postgresDb.updateRole(id, request);
        elasticsearchDb.saveLog("role updated: " + id, origin);
        return role;
    }

    public void delete(UUID id, Origin origin) {
        postgresDb.deleteRole(id);
        elasticsearchDb.saveLog("role deleted: " + id, origin);
    }

    public UserRoleResponse assignRoleToUser(UserRoleRequest request, Origin origin) {
        var userRole = postgresDb.assignRole(request);
        elasticsearchDb.saveLog("role assigned: user=" + request.userId() + " role=" + request.roleId(), origin);
        return userRole;
    }

    public List<UserRoleResponse> findRolesByUserId(UUID userId) {
        return postgresDb.findRolesByUserId(userId);
    }

    public void revokeRole(UUID userId, UUID roleId, Origin origin) {
        postgresDb.revokeRole(userId, roleId);
        elasticsearchDb.saveLog("role revoked: user=" + userId + " role=" + roleId, origin);
    }

    public List<String> findRoleNamesByUserId(UUID userId) {
        return postgresDb.findRoleNamesByUserId(userId);
    }
}
