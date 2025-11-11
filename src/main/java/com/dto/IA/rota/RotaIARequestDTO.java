package com.dto.IA.rota;

import com.domain.user.Enum.Periodo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record RotaIARequestDTO(
        Integer idCidade,
        String nome,
        String cidadeNome,
        Periodo periodo,
        Integer capacidade,
        Boolean ativo,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horaPartida,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horaChegada

) {}
