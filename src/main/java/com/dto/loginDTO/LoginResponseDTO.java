package com.dto.loginDTO;

import java.util.UUID;

public record LoginResponseDTO(
        UUID idColaborador,
        String nome,
        String token,
        String email,

        String role){

}
