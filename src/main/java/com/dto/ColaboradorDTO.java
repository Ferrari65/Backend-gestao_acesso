package com.dto;

import com.domain.user.User;
import java.util.UUID;

public record ColaboradorDTO (        UUID idColaborador,
                                      String email,
                                      String role
) {
    public static ColaboradorDTO from(User u) {
        return new ColaboradorDTO(
                u.getIdColaborador(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().getNome() : null
        );
    }
}