package com.matheushrs.psp_void_postgres_db.config;

import com.matheushrs.psp_void_postgres_db.entity.RoleEntity;
import com.matheushrs.psp_void_postgres_db.entity.UserEntity;
import com.matheushrs.psp_void_postgres_db.entity.UserRoleEntity;
import com.matheushrs.psp_void_postgres_db.repository.RoleRepository;
import com.matheushrs.psp_void_postgres_db.repository.UserRepository;
import com.matheushrs.psp_void_postgres_db.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeederConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin.password}")
    private String adminPassword;

    @Value("${seed.service-account.password}")
    private String serviceAccountPassword;

    @Bean
    public ApplicationRunner seedDefaultData() {
        return args -> {
            // ── Roles ──────────────────────────────────────────────────────────
            var adminRole    = createRoleIfAbsent("ADMIN");
            var customerRole = createRoleIfAbsent("CUSTOMER");

            // ── Users ──────────────────────────────────────────────────────────
            var adminUser   = createUserIfAbsent("Admin",         "admin@pspvoid.com",      adminPassword);
            var toolsUser   = createUserIfAbsent("Tools Service", "tools@pspvoid.internal", serviceAccountPassword);

            // ── Assignments ────────────────────────────────────────────────────
            assignRoleIfAbsent(adminUser, adminRole);
            assignRoleIfAbsent(toolsUser, adminRole);  // service account also needs ADMIN to manage tools

            log.info("Default data seeded successfully.");
        };
    }

    private RoleEntity createRoleIfAbsent(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            var role = roleRepository.save(RoleEntity.builder().name(name).build());
            log.info("Created role: {}", name);
            return role;
        });
    }

    private UserEntity createUserIfAbsent(String name, String email, String rawPassword) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            var user = userRepository.save(UserEntity.builder()
                    .name(name)
                    .email(email)
                    .passwordHash(passwordEncoder.encode(rawPassword))
                    .status(true)
                    .build());
            log.info("Created user: {}", email);
            return user;
        });
    }

    private void assignRoleIfAbsent(UserEntity user, RoleEntity role) {
        boolean exists = !userRoleRepository.findByUserId(user.getId()).isEmpty()
                && userRoleRepository.findByUserId(user.getId()).stream()
                        .anyMatch(ur -> ur.getRoleId().equals(role.getId()));
        if (!exists) {
            userRoleRepository.save(UserRoleEntity.builder()
                    .userId(user.getId())
                    .roleId(role.getId())
                    .build());
            log.info("Assigned role {} to user {}", role.getName(), user.getEmail());
        }
    }
}
