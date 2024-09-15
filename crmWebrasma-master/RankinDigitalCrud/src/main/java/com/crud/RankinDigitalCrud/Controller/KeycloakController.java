package com.crud.RankinDigitalCrud.Controller;

import com.crud.RankinDigitalCrud.dto.UserDTO;
import com.crud.RankinDigitalCrud.Service.KeycloakAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController {

    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    public KeycloakController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = keycloakAdminService.getAllUsers();
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO userDTO = keycloakAdminService.getUserByUsername(username);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
