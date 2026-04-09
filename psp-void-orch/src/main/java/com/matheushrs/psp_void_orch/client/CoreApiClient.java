package com.matheushrs.psp_void_orch.client;

import com.matheushrs.psp_void_orch.dto.*;
import com.matheushrs.psp_void_orch.dto.UserCredentialsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoreApiClient {

    @Qualifier("coreWebClient")
    private final WebClient webClient;

    // ── Products ──────────────────────────────────────────────────────────────

    public ProductResponse createProduct(ProductRequest request, String origin) {
        return webClient.post().uri("/products").header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(ProductResponse.class).block();
    }

    public List<ProductResponse> findAllProducts() {
        return webClient.get().uri("/products").retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductResponse>>() {}).block();
    }

    public ProductResponse findProductById(UUID id) {
        return webClient.get().uri("/products/{id}", id).retrieve()
                .bodyToMono(ProductResponse.class).block();
    }

    public ProductResponse updateProduct(UUID id, ProductRequest request, String origin) {
        return webClient.put().uri("/products/{id}", id).header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(ProductResponse.class).block();
    }

    public void deleteProduct(UUID id, String origin) {
        webClient.delete().uri("/products/{id}", id).header("X-Origin", origin)
                .retrieve().toBodilessEntity().block();
    }

    // ── Prices ────────────────────────────────────────────────────────────────

    public ProductPriceResponse createPrice(ProductPriceRequest request, String origin) {
        return webClient.post().uri("/prices").header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(ProductPriceResponse.class).block();
    }

    public List<ProductPriceResponse> findAllPrices() {
        return webClient.get().uri("/prices").retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductPriceResponse>>() {}).block();
    }

    public ProductPriceResponse findPriceById(UUID id) {
        return webClient.get().uri("/prices/{id}", id).retrieve()
                .bodyToMono(ProductPriceResponse.class).block();
    }

    public ProductPriceResponse updatePrice(UUID id, ProductPriceRequest request, String origin) {
        return webClient.put().uri("/prices/{id}", id).header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(ProductPriceResponse.class).block();
    }

    public void deletePrice(UUID id, String origin) {
        webClient.delete().uri("/prices/{id}", id).header("X-Origin", origin)
                .retrieve().toBodilessEntity().block();
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    public UserResponse createUser(UserRequest request, String origin) {
        return webClient.post().uri("/users").header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(UserResponse.class).block();
    }

    public List<UserResponse> findAllUsers() {
        return webClient.get().uri("/users").retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserResponse>>() {}).block();
    }

    public UserResponse findUserById(UUID id) {
        return webClient.get().uri("/users/{id}", id).retrieve()
                .bodyToMono(UserResponse.class).block();
    }

    public UserResponse updateUser(UUID id, UserRequest request, String origin) {
        return webClient.put().uri("/users/{id}", id).header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(UserResponse.class).block();
    }

    public void deleteUser(UUID id, String origin) {
        webClient.delete().uri("/users/{id}", id).header("X-Origin", origin)
                .retrieve().toBodilessEntity().block();
    }

    // ── Roles ─────────────────────────────────────────────────────────────────

    public RoleResponse createRole(RoleRequest request, String origin) {
        return webClient.post().uri("/roles").header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(RoleResponse.class).block();
    }

    public List<RoleResponse> findAllRoles() {
        return webClient.get().uri("/roles").retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RoleResponse>>() {}).block();
    }

    public RoleResponse findRoleById(UUID id) {
        return webClient.get().uri("/roles/{id}", id).retrieve()
                .bodyToMono(RoleResponse.class).block();
    }

    public RoleResponse updateRole(UUID id, RoleRequest request, String origin) {
        return webClient.put().uri("/roles/{id}", id).header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(RoleResponse.class).block();
    }

    public void deleteRole(UUID id, String origin) {
        webClient.delete().uri("/roles/{id}", id).header("X-Origin", origin)
                .retrieve().toBodilessEntity().block();
    }

    public UserRoleResponse assignRole(UserRoleRequest request, String origin) {
        return webClient.post().uri("/roles/assign").header("X-Origin", origin).bodyValue(request)
                .retrieve().bodyToMono(UserRoleResponse.class).block();
    }

    public List<UserRoleResponse> findRolesByUserId(UUID userId) {
        return webClient.get().uri("/roles/by-user/{userId}", userId).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserRoleResponse>>() {}).block();
    }

    public void revokeRole(UUID userId, UUID roleId, String origin) {
        webClient.delete()
                .uri(u -> u.path("/roles/revoke")
                        .queryParam("userId", userId)
                        .queryParam("roleId", roleId)
                        .build())
                .header("X-Origin", origin)
                .retrieve().toBodilessEntity().block();
    }

    public UserResponse authenticateUser(UserCredentialsRequest request) {
        return webClient.post().uri("/users/authenticate").bodyValue(request)
                .retrieve().bodyToMono(UserResponse.class).block();
    }

    public List<String> findUserRoleNames(UUID userId) {
        return webClient.get().uri("/roles/names-by-user/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    // ── Logs ──────────────────────────────────────────────────────────────────

    public List<LogResponse> findAllLogs() {
        return webClient.get().uri("/logs").retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<LogResponse>>() {}).block();
    }
}
