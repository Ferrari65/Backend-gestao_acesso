package com.infra;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${security.jwt.issuer:auth.api}")
    private String issuer;

    public String GenerateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String roleName = (user.getRole() == null || user.getRole().getNome() == null)
                    ? null
                    : user.getRole().getNome().trim().toUpperCase();

            var builder = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .withClaim("uid", user.getIdColaborador().toString());

            if (roleName != null) {
                builder.withArrayClaim("roles", new String[]{ roleName });
            }
            return builder.sign(algorithm);

        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .acceptLeeway(3)
                    .build()
                    .verify(token)
                    .getSubject();
            return (subject == null || subject.isBlank()) ? null : subject.trim();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
