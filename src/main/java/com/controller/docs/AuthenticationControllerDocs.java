package com.controller.docs;

import com.dto.auth.ForgotPasswordRequest;
import com.dto.auth.ResetPasswordRequest;
import com.dto.loginDTO.LoginRequestDTO;
import com.dto.loginDTO.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthenticationControllerDocs {

    @Operation(
            summary = "Solicitar redefinição de senha",
            description = "Envia um e-mail ao usuário com o link para redefinir a senha."
    )
    @ApiResponse(responseCode = "200", description = "E-mail enviado com instruções")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    ResponseEntity<Void> forgot(@RequestBody @Valid ForgotPasswordRequest req);

    @Operation(
            summary = "Redefinir senha",
            description = "Permite redefinir a senha do usuário utilizando o token recebido por e-mail."
    )
    @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso")
    @ApiResponse(responseCode = "400", description = "Token inválido ou senha não atende aos requisitos", content = @Content)
    ResponseEntity<Void> reset(@RequestBody @Valid ResetPasswordRequest req);

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
