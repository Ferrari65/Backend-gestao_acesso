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
        String email = (username == null) ? "" : username.trim();
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário inexistente ou senha inválida"));
    }
}
