package com.dto.PATCH;

import com.domain.user.Enum.Periodo;
import com.dto.localizacao.Rota.RotaPontoItemRequestDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.List;

public record RotaPatchDTO(
        String nome,
        Periodo periodo,
        Integer idCidade,
        Integer capacidade,
        Boolean ativo,
        @Schema(example = "06:15:00") String horaPartida,
        @Schema(example = "06:55:00") String horaChegada,
        List<RotaPontoItemRequestDTO> pontos
) {}

