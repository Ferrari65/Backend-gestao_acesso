package com.controller;

import com.domain.user.colaborador.User;
import com.dto.LoginDTO.LoginRequestDTO;
import com.dto.LoginDTO.LoginResponseDTO;
import com.infra.TokenService;
import com.services.LoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final TokenService tokenService;
    private final LoginService loginService;

    @PostMapping("/login")
    @Tag(name="Login", description = "Endpoint de Login")

    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO body) {
        User user = loginService.loginAuto(body.username(), body.senha());

        String authMode = body.username().contains("@") ? "email" : "matricula";
        String token = tokenService.GenerateToken(user, authMode);


        String role = (user.getRole() != null && user.getRole().getNome() != null)
                ? user.getRole().getNome().trim().toUpperCase()
                : "COLABORADOR";

        String homePath = switch (role) {
            case "GESTOR" -> "/gestor";
            case "LIDER"  -> "/lider";
            default       -> "/colab";
        };

        return ResponseEntity.ok(new LoginResponseDTO(token, user.getEmail(), role, homePath));
    }
}
