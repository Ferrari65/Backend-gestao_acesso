package com.services;

import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorizationService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String raw = (username == null) ? "" : username.trim();
        boolean isEmail = raw.contains("@");

        var userOpt = isEmail
                ? repository.findByEmail(raw.toLowerCase())
                : repository.findByMatricula(raw);

        var user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("Credenciais inválidas"));

        if (Boolean.FALSE.equals(user.getAtivo())) {
            throw new org.springframework.security.authentication.DisabledException("Usuário inativo");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(isEmail ? user.getEmail() : user.getMatricula())
                .password(user.getSenha())
                .authorities(user.getAuthorities()) // ROLE_COLABORADOR etc.
                .accountExpired(false).accountLocked(false)
                .credentialsExpired(false).disabled(!user.getAtivo())
                .build();
    }

    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Sem usuário autenticado");
        }
        String username = auth.getName(); // email OU matrícula
        String raw = (username == null) ? "" : username.trim();
        boolean isEmail = raw.contains("@");

        var userOpt = isEmail
                ? repository.findByEmail(raw.toLowerCase())
                : repository.findByMatricula(raw);

        var user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("Usuário não encontrado para: " + raw));

        return user.getIdColaborador();
    }
}
