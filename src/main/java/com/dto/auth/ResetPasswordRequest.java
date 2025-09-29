package com.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @Schema(writeOnly = true, example = "a1b2c3...") @NotBlank String token,
        @Schema(writeOnly = true, example = "NovaSenha@123")
        @NotBlank @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String newPassword
) {}