// com.controller.AuthenticationController
package com.controller;

import com.domain.user.colaborador.User;                 // <- cuidado para não importar o User do Spring
import com.dto.LoginDTO.LoginRequestDTO;              // record LoginRequestDTO(String email, String senha)
import com.dto.LoginDTO.LoginResponseDTO;             // record LoginResponseDTO(String token, String email, String role, String homePath)
import com.infra.TokenService;
import com.services.LoginService;
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO body) {
        User user = loginService.loginAuto(body.email(), body.senha());

        String token = tokenService.GenerateToken(user);
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
