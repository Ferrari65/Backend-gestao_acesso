package com.dto.localizacao.Viagem;

import com.domain.user.Enum.TipoViagem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;


public record ViagemRotaRequestDTO(

        Integer idRota,
        Integer idMotorista,
        Integer idVeiculo,
        LocalDate data,
        LocalTime saidaPrevista,
        LocalTime  chegadaPrevista,
        TipoViagem tipoViagem
) {}
