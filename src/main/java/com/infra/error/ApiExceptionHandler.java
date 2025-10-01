package com.infra.error;

import com.exceptions.AuthLoginException;
import com.exceptions.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AuthLoginException.class)
    public ResponseEntity<ProblemDetail> handleAuth(AuthLoginException ex,
                                                    HttpServletRequest req ){

        HttpStatus status = switch (ex.getCode()) {
            case "AUTH_CREDENCIAIS_INVALIDAS" -> HttpStatus.UNAUTHORIZED;
            case "AUTH_CONTA_BLOQUEADA"       -> HttpStatus.LOCKED;
            case "AUTH_TENTATIVAS_EXCEDIDAS"  -> HttpStatus.TOO_MANY_REQUESTS;
            default                           -> HttpStatus.UNAUTHORIZED;
        };

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, ex.getUserMessage());
        pd.setTitle("Falha de autenticação");

        pd.setProperty("code", ex.getCode());
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        pd.setProperty("statusText", status.getReasonPhrase());

        return  ResponseEntity.status(status).body(pd);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<?> handleRegra(RegraNegocioException ex, HttpServletRequest req) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setTitle("Regra de negócio violada");
        pd.setProperty("code", ex.getCode());
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return ResponseEntity.unprocessableEntity().body(pd);
    }
    private record ErrorResponse(String code, String message) {}
}

