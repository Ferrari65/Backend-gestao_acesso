package com.dto.Motorista;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record MotoristaResponseDTO (

        @Schema(description = "Identificador do motorista")
        Long id,

        String nome,
        String cnh,
        String telefone,
        String empresaTerceiro,

        @Schema(description = "Data de vencimento da CNH")
        LocalDate dataVencCnh
){}
