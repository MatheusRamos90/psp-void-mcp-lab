package com.matheushrs.psp_void_orch.service;

import com.matheushrs.psp_void_orch.client.CoreApiClient;
import com.matheushrs.psp_void_orch.dto.LoginRequest;
import com.matheushrs.psp_void_orch.dto.TokenResponse;
import com.matheushrs.psp_void_orch.dto.UserCredentialsRequest;
import com.matheushrs.psp_void_orch.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CoreApiClient coreClient;
    private final JwtUtil jwtUtil;

    public TokenResponse login(LoginRequest request) {
        var user = coreClient.authenticateUser(
                new UserCredentialsRequest(request.email(), request.password()));

        List<String> roles = coreClient.findUserRoleNames(user.id());

        String token = jwtUtil.generate(user.id(), user.email(), roles);

        return new TokenResponse(token, jwtUtil.getExpirationMs(), user.id(), user.email(), roles);
    }
}
