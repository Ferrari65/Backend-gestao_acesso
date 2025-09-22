package com.controller;

import com.dto.auth.ForgotPasswordRequest;
import com.dto.auth.ResetPasswordRequest;
import com.dto.loginDTO.LoginRequestDTO;
import com.dto.loginDTO.LoginResponseDTO;
import com.infra.TokenService;
import com.services.auth.util.PasswordResetService;
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
@Tag(name = "Autenticação", description = "Gerenciamento de login e recuperação de senha")
public class AuthenticationController implements com.controller.docs.AuthenticationControllerDocs {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetService resetService;

    @PostMapping("/forgot-password")
    @Override
    public ResponseEntity<Void> forgot(@RequestBody @Valid ForgotPasswordRequest req) {
        resetService.forgotPassword(req.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Override
    public ResponseEntity<Void> reset(@RequestBody @Valid ResetPasswordRequest req) {
        resetService.resetPassword(req.token(), req.newPassword());
        return ResponseEntity.noContent().build();
    }

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
