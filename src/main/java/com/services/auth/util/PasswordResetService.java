package com.services.auth.util;

import com.domain.user.auth.util.PasswordResetToken;
import com.domain.user.colaborador.User;
import com.infra.config.AppProps;
import com.repositories.UserRepository;
import com.repositories.auth.PasswordResetTokenRepository;
import com.services.email.EmailSender;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final @Qualifier("smtpEmailSender") EmailSender emailSender;
    private final PasswordEncoder encoder;
    private final AppProps appProps;

    @Transactional(readOnly = true)
    public void validateTokenOnly(String rawToken) {
        var tokenHash = SecurityUtils.sha256Hex(rawToken);
        boolean ok = tokenRepo
                .findByTokenHashAndUsedFalseAndExpiresAtAfter(tokenHash, Instant.now())
                .isPresent();
        if (!ok) throw new EntityNotFoundException("Token inválido ou expirado.");
    }

    @Transactional
    public void forgotPassword(String email) {
        var userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) return;

        String rawToken  = SecurityUtils.generateRawToken();
        String tokenHash = SecurityUtils.sha256Hex(rawToken);

        var prt = new PasswordResetToken();
        prt.setEmail(email);
        prt.setTokenHash(tokenHash);
        prt.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        tokenRepo.save(prt);

        String resetUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/auth/reset-password")
                .queryParam("token", rawToken)
                .build(true)
                .toUriString();

        String html = """
            <p>Olá,</p>
            <p>Recebemos um pedido para redefinir sua senha.</p>
            <p><a href="%s">Clique aqui para redefinir sua senha</a></p>
            <p>Ou copie e cole no navegador:</p>
            <p>%s</p>
            <p>Este link expira em 30 minutos. Se você não solicitou, ignore este e-mail.</p>
        """.formatted(resetUrl, resetUrl);

        emailSender.send(email, "Redefinição de senha", html);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = SecurityUtils.sha256Hex(rawToken);

        var prt = tokenRepo.findByTokenHashAndUsedFalseAndExpiresAtAfter(tokenHash, Instant.now())
                .orElseThrow(() -> new EntityNotFoundException("Token inválido ou expirado."));

        User user = userRepo.findByEmail(prt.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        user.setSenha(encoder.encode(newPassword));
        userRepo.save(user);

        prt.setUsed(true);
        tokenRepo.save(prt);
    }
}
