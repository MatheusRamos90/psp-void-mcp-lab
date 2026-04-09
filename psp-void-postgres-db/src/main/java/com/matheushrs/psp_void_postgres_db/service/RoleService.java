package com.matheushrs.psp_void_postgres_db.service;

import com.matheushrs.psp_void_postgres_db.dto.RoleRequest;
import com.matheushrs.psp_void_postgres_db.dto.RoleResponse;
import com.matheushrs.psp_void_postgres_db.entity.RoleEntity;
import com.matheushrs.psp_void_postgres_db.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public RoleResponse create(RoleRequest request) {
        var entity = RoleEntity.builder().name(request.name()).build();
        return toResponse(repository.save(entity));
    }

    public List<RoleResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public RoleResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Role not found: " + id));
    }

    public RoleResponse update(UUID id, RoleRequest request) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Role not found: " + id));
        entity.setName(request.name());
        return toResponse(repository.save(entity));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Role not found: " + id);
        }
        repository.deleteById(id);
    }

    private RoleResponse toResponse(RoleEntity e) {
        return new RoleResponse(e.getId(), e.getName(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
