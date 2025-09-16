package com.controller;

import com.dto.loginDTO.LoginRequestDTO;
import com.dto.loginDTO.LoginResponseDTO;
import com.infra.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name="Login", description = "Endpoint de Login")
public class AuthenticationController implements com.controller.docs.AuthenticationControllerDocs {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @Override
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO body) {
        var authToken = new UsernamePasswordAuthenticationToken(body.username(), body.senha());
        var auth = authenticationManager.authenticate(authToken);

        var user = (com.domain.user.colaborador.User) auth.getPrincipal();
        var role = (user.getRole() != null && user.getRole().getNome() != null)
                ? user.getRole().getNome().trim().toUpperCase()
                : "COLABORADOR";

        var homePath = switch (role) {
            case "GESTOR" -> "/gestor";
            case "LIDER"  -> "/lider";
            default       -> "/colab";
        };

        var authMode = body.username().contains("@") ? "email" : "matricula";
        var token = tokenService.generateToken(user, authMode);

        return ResponseEntity.ok(new LoginResponseDTO(token, user.getEmail(), role, homePath));
    }
}
