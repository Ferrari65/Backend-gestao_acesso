package com.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record ForgotPasswordResponse(
        @Schema(example = "Se o e-mail existir, você receberá um link para redefinir a senha.")
        String message,

        @Schema(description = "Somente em DEV: link direto para testar o reset no backend",
                example = "http://localhost:8080/auth/reset-password?token=abc123...")
        String devResetLink
) {}