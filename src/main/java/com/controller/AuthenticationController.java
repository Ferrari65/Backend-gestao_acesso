package com.controller;

import com.dto.GestorLoginRequestDTO.GestorLoginRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid GestorLoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                data.email(),
                data.senha()
        );
        return ResponseEntity.ok().build();
    }
}
