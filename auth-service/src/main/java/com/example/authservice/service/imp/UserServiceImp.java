package com.example.authservice.service.imp;

import com.example.authservice.dto.req.LoginReq;
import com.example.authservice.dto.req.UserRegisterReq;
import com.example.authservice.dto.res.LoginRes;
import com.example.authservice.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final Keycloak keycloak;
    private final WebClient.Builder webClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Override
    public void createUser(UserRegisterReq req) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(req.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() != 201) {
            String error = response.readEntity(String.class);
            log.error("Create user error {}", error);
            throw new RuntimeException("Khong the tao user trong keycloak");
        }

    }

    @Override
    public LoginRes login(LoginReq req) {
        String tokenUrl = keycloakUrl
                + "/realms/"
                + realm
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", req.getUsername());
        formData.add("password", req.getPassword());

        return webClient.build().post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<LoginRes> () { // parse response về kiểu LoginRes
                })
                .block();
    }
}
