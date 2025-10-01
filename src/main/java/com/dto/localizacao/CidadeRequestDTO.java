package com.dto.localizacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CidadeRequestDTO (
        @NotBlank
        @Size (max =  255)
        String nome,

        @NotBlank
        @Pattern(regexp = "^[A-Za-z]{2}$") String uf
){}
