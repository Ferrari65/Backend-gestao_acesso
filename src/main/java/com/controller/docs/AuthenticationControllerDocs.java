package com.controller.docs;

import com.dto.loginDTO.LoginRequestDTO;
import com.dto.loginDTO.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthenticationControllerDocs {

    @Operation(
            summary = "Autenticar usuário",
            description = "Autentica um usuário com base nas credenciais fornecidas (nome de usuário/matrícula e senha), retornando um token de autenticação (JWT).",
            responses = {
                    @ApiResponse(
                            description = "Autenticação bem-sucedida, retorna o token de acesso.",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = LoginResponseDTO.class)
                                    )
                            }),
                    @ApiResponse(description = "Credenciais inválidas. O nome de usuário ou a senha estão incorretos.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Requisição inválida. A validação do DTO falhou.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor.", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO body);
}
