package com.services;

import com.domain.user.colaborador.User;
import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User loginAuto(String username, String senha) {
        String normalized = (username == null) ? "" : username.trim().toLowerCase();

        User user;
        if(normalized.contains("@")){
            user = userRepository.findByEmail(normalized.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("EMAIL INVALIDO"));
        }else{
            user = userRepository.findByMatricula(normalized)
                    .orElseThrow(() -> new RuntimeException("MATRICULA INVALIDA"));
        }

        if (Boolean.FALSE.equals(user.getAtivo())) {
            throw new RuntimeException("USUARIO INATIVO");
        }
        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new RuntimeException("SENHA INVALIDA");
        }
        return user;
    }
}