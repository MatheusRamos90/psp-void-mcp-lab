package com.matheushrs.psp_void_core.service;

import com.matheushrs.psp_void_core.client.ElasticsearchDbClient;
import com.matheushrs.psp_void_core.client.PostgresDbClient;
import com.matheushrs.psp_void_core.dto.UserCredentialsRequest;
import com.matheushrs.psp_void_core.dto.UserRequest;
import com.matheushrs.psp_void_core.dto.UserResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PostgresDbClient postgresDb;
    private final ElasticsearchDbClient elasticsearchDb;

    public UserResponse create(UserRequest request, Origin origin) {
        var user = postgresDb.createUser(request);
        elasticsearchDb.saveLog("user created: " + user.id(), origin);
        return user;
    }

    public List<UserResponse> findAll() {
        return postgresDb.findAllUsers();
    }

    public UserResponse findById(UUID id) {
        return postgresDb.findUserById(id);
    }

    public UserResponse update(UUID id, UserRequest request, Origin origin) {
        var user = postgresDb.updateUser(id, request);
        elasticsearchDb.saveLog("user updated: " + id, origin);
        return user;
    }

    public void delete(UUID id, Origin origin) {
        postgresDb.deleteUser(id);
        elasticsearchDb.saveLog("user deleted: " + id, origin);
    }

    public UserResponse authenticate(UserCredentialsRequest request) {
        return postgresDb.authenticateUser(request);
    }
}
