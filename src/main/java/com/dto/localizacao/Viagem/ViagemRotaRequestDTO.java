package com.dto.localizacao.Viagem;

import com.domain.user.Enum.TipoViagem;

import java.time.LocalDate;
import java.time.OffsetDateTime;


public record ViagemRotaRequestDTO(

        Integer idRota,
        Integer idMotorista,
        Integer idVeiculo,
        LocalDate saidaPrevista,
        LocalDate chegadaPrevista,
        TipoViagem tipoViagem,
        OffsetDateTime createdAt
) {}
