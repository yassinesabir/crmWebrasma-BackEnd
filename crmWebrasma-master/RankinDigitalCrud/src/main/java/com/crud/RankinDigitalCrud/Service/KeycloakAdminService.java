package com.crud.RankinDigitalCrud.Service;

import com.crud.RankinDigitalCrud.Entity.User;
import com.crud.RankinDigitalCrud.Repository.UserRepository;
import com.crud.RankinDigitalCrud.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakAdminService {

    private Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
        syncUsers();
    }

    public void syncUsers() {
        List<UserRepresentation> keycloakUsers = keycloak.realm(realm).users().list();
        for (UserRepresentation keycloakUser : keycloakUsers) {
            User user = new User(
                    keycloakUser.getUsername(),
                    keycloakUser.getFirstName(),
                    keycloakUser.getLastName(),
                    keycloakUser.getEmail()
            );
            userRepository.findByUsername(keycloakUser.getUsername())
                    .map(existingUser -> {
                        // Update existing user
                        existingUser.setFirstName(user.getFirstName());
                        existingUser.setLastName(user.getLastName());
                        existingUser.setEmail(user.getEmail());
                        return userRepository.save(existingUser);
                    })
                    .orElseGet(() -> {
                        // Save new user
                        return userRepository.save(user);
                    });
        }
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()))
                .collect(Collectors.toList());
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            return new UserDTO(
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail());
        }
        return null;
    }
}
