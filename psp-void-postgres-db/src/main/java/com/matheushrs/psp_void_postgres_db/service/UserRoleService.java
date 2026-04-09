package com.matheushrs.psp_void_postgres_db.service;

import com.matheushrs.psp_void_postgres_db.dto.UserRoleRequest;
import com.matheushrs.psp_void_postgres_db.dto.UserRoleResponse;
import com.matheushrs.psp_void_postgres_db.entity.UserRoleEntity;
import com.matheushrs.psp_void_postgres_db.repository.RoleRepository;
import com.matheushrs.psp_void_postgres_db.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository repository;
    private final RoleRepository roleRepository;

    public UserRoleResponse assign(UserRoleRequest request) {
        var entity = UserRoleEntity.builder()
                .userId(request.userId())
                .roleId(request.roleId())
                .build();
        return toResponse(repository.save(entity));
    }

    public List<UserRoleResponse> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public List<UserRoleResponse> findByRoleId(UUID roleId) {
        return repository.findByRoleId(roleId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void revoke(UUID userId, UUID roleId) {
        repository.deleteByUserIdAndRoleId(userId, roleId);
    }

    /**
     * Returns the role names (e.g. "ADMIN", "CUSTOMER") assigned to a given user.
     * Used during JWT generation to embed roles as claims.
     */
    public List<String> findRoleNamesByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(ur -> roleRepository.findById(ur.getRoleId()))
                .filter(java.util.Optional::isPresent)
                .map(opt -> opt.get().getName())
                .toList();
    }

    private UserRoleResponse toResponse(UserRoleEntity e) {
        return new UserRoleResponse(e.getId(), e.getUserId(), e.getRoleId(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
}
