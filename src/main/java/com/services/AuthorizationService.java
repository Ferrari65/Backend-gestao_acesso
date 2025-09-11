package com.services;

import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return user;
    }
}
