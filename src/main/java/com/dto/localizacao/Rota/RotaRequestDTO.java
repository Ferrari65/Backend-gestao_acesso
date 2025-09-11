package com.dto.localizacao.Rota;

import com.domain.user.Enum.Periodo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;

public record RotaRequestDTO (
    Integer idCidade,
    String nome,
    Periodo periodo,
    Integer capacidade,
    Boolean ativo,

    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime horaPartida,
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime horaChegada,

    @Valid
    @NotNull @Size(min = 2, message = "A rota deve ter pelo menos origem e destino")
    List<RotaPontoItemRequestDTO> pontos
    )
{}
