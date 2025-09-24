package com.exceptions;

public class AuthLoginException extends RuntimeException {
    private final String code;
    private final String userMessage;

    public AuthLoginException(String code, String userMessage) {
        super(userMessage);
        this.code = code;
        this.userMessage = userMessage;
    }

    public String getCode() { return code; }
    public String getUserMessage() { return userMessage; }

    public static AuthLoginException invalidCredentials() {
        return new AuthLoginException("AUTH_CREDENCIAIS_INVALIDAS", "E-mail ou senha inválidos.");
    }
    public static AuthLoginException accountLocked() {
        return new AuthLoginException("AUTH_CONTA_BLOQUEADA", "Conta temporariamente bloqueada.");
    }
    public static AuthLoginException tooManyAttempts() {
        return new AuthLoginException("AUTH_TENTATIVAS_EXCEDIDAS", "Muitas tentativas. Tente novamente em alguns minutos.");
    }
}
