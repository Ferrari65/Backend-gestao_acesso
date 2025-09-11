package com.dto.loginDTO;

public record LoginResponseDTO(
        String token,
        String email,
        String role,
        String homePath){

}
