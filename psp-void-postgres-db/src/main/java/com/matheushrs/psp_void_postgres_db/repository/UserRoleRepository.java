package com.matheushrs.psp_void_postgres_db.repository;

import com.matheushrs.psp_void_postgres_db.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {

    List<UserRoleEntity> findByUserId(UUID userId);

    List<UserRoleEntity> findByRoleId(UUID roleId);

    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
