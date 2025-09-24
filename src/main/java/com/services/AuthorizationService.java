package com.services;

import com.domain.user.colaborador.User;
import com.exceptions.AuthLoginException;
import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String raw = username == null ? "" : username.trim();
        boolean isEmail = raw.contains("@");

        Optional<User> userOpt = isEmail
                ? repository.findByEmail(raw.toLowerCase())
                : repository.findByMatricula(raw);

        User user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("Credenciais inválidas"));

        Boolean ativo = user.getAtivo();
        if (ativo != null && !ativo) {
            throw new DisabledException("Usuário inativo");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User authenticate(String usernameOrMatriculaOrEmail, String rawPassword) {
        String raw = usernameOrMatriculaOrEmail == null ? "" : usernameOrMatriculaOrEmail.trim();
        boolean isEmail = raw.contains("@");

        var userOpt = isEmail
                ? repository.findByEmail(raw.toLowerCase())
                : repository.findByMatricula(raw);

        var user = userOpt.orElseThrow(AuthLoginException::invalidCredentials);

        if (Boolean.FALSE.equals(user.getAtivo())) {
            throw AuthLoginException.accountLocked();
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw AuthLoginException.invalidCredentials();
        }

        return user;
    }
    @Transactional(readOnly = true)
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Sem usuário autenticado");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User u) {
            return u.getIdColaborador();
        }

        String username = auth.getName();
        String raw = username == null ? "" : username.trim();
        boolean isEmail = raw.contains("@");

        User user = (isEmail
                ? repository.findByEmail(raw.toLowerCase())
                : repository.findByMatricula(raw))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para: " + raw));

        return user.getIdColaborador();
    }
}
