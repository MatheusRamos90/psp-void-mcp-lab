package com.matheushrs.psp_void_core.client;

import com.matheushrs.psp_void_core.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostgresDbClient {

    @Qualifier("postgresDbWebClient")
    private final WebClient webClient;

    // ── Products ──────────────────────────────────────────────────────────────

    public ProductResponse createProduct(ProductRequest request) {
        return webClient.post().uri("/products").bodyValue(request)
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

    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        return webClient.put().uri("/products/{id}", id).bodyValue(request)
                .retrieve().bodyToMono(ProductResponse.class).block();
    }

    public void deleteProduct(UUID id) {
        webClient.delete().uri("/products/{id}", id).retrieve().toBodilessEntity().block();
    }

    // ── Prices ────────────────────────────────────────────────────────────────

    public ProductPriceResponse createPrice(ProductPriceRequest request) {
        return webClient.post().uri("/prices").bodyValue(request)
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

    public ProductPriceResponse updatePrice(UUID id, ProductPriceRequest request) {
        return webClient.put().uri("/prices/{id}", id).bodyValue(request)
                .retrieve().bodyToMono(ProductPriceResponse.class).block();
    }

    public void deletePrice(UUID id) {
        webClient.delete().uri("/prices/{id}", id).retrieve().toBodilessEntity().block();
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    public UserResponse createUser(UserRequest request) {
        return webClient.post().uri("/users").bodyValue(request)
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

    public UserResponse updateUser(UUID id, UserRequest request) {
        return webClient.put().uri("/users/{id}", id).bodyValue(request)
                .retrieve().bodyToMono(UserResponse.class).block();
    }

    public void deleteUser(UUID id) {
        webClient.delete().uri("/users/{id}", id).retrieve().toBodilessEntity().block();
    }

    public UserResponse authenticateUser(UserCredentialsRequest request) {
        return webClient.post().uri("/users/authenticate").bodyValue(request)
                .retrieve().bodyToMono(UserResponse.class).block();
    }

    // ── Roles ─────────────────────────────────────────────────────────────────

    public RoleResponse createRole(RoleRequest request) {
        return webClient.post().uri("/roles").bodyValue(request)
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

    public RoleResponse updateRole(UUID id, RoleRequest request) {
        return webClient.put().uri("/roles/{id}", id).bodyValue(request)
                .retrieve().bodyToMono(RoleResponse.class).block();
    }

    public void deleteRole(UUID id) {
        webClient.delete().uri("/roles/{id}", id).retrieve().toBodilessEntity().block();
    }

    // ── UserRoles ─────────────────────────────────────────────────────────────

    public UserRoleResponse assignRole(UserRoleRequest request) {
        return webClient.post().uri("/user-roles").bodyValue(request)
                .retrieve().bodyToMono(UserRoleResponse.class).block();
    }

    public List<UserRoleResponse> findRolesByUserId(UUID userId) {
        return webClient.get().uri("/user-roles/by-user/{userId}", userId).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserRoleResponse>>() {}).block();
    }

    public void revokeRole(UUID userId, UUID roleId) {
        webClient.delete()
                .uri(u -> u.path("/user-roles").queryParam("userId", userId).queryParam("roleId", roleId).build())
                .retrieve().toBodilessEntity().block();
    }

    public List<String> findRoleNamesByUserId(UUID userId) {
        return webClient.get().uri("/user-roles/names-by-user/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }
}
