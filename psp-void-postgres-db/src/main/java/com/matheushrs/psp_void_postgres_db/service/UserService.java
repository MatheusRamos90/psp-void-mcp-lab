package com.matheushrs.psp_void_postgres_db.service;

import com.matheushrs.psp_void_postgres_db.dto.UserCredentialsRequest;
import com.matheushrs.psp_void_postgres_db.dto.UserRequest;
import com.matheushrs.psp_void_postgres_db.dto.UserResponse;
import com.matheushrs.psp_void_postgres_db.entity.UserEntity;
import com.matheushrs.psp_void_postgres_db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        var entity = UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(request.status() != null ? request.status() : true)
                .build();
        return toResponse(repository.save(entity));
    }

    public List<UserResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    public UserResponse update(UUID id, UserRequest request) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        entity.setName(request.name());
        entity.setEmail(request.email());
        if (request.status() != null) entity.setStatus(request.status());
        if (request.password() != null && !request.password().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toResponse(repository.save(entity));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }
        repository.deleteById(id);
    }

    public UserResponse authenticate(UserCredentialsRequest request) {
        var entity = repository.findByEmail(request.email())
                .orElseThrow(() -> new NoSuchElementException("Invalid credentials"));
        if (!entity.getStatus()) {
            throw new IllegalStateException("User is inactive");
        }
        if (!passwordEncoder.matches(request.password(), entity.getPasswordHash())) {
            throw new NoSuchElementException("Invalid credentials");
        }
        return toResponse(entity);
    }

    private UserResponse toResponse(UserEntity e) {
        return new UserResponse(e.getId(), e.getName(), e.getEmail(),
                e.getStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
