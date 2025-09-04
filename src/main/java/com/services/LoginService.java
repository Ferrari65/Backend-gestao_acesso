package com.services;

import com.domain.user.User;
import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User loginAuto(String email, String senha) {
        String normalized = (email == null) ? "" : email.trim().toLowerCase();

        var user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new RuntimeException("CREDENCIAL INVALIDA"));

        if (Boolean.FALSE.equals(user.getAtivo())) {
            throw new RuntimeException("USUARIO INATIVO");
        }
        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new RuntimeException("SENHA INVALIDA");
        }
        return user;
    }
}