package com.dto.colaborador;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FormResponse(
        UUID idForm,
        UUID idColaborador,
        String nome,
        String codigo,
        Integer idCidade,
        Integer idRotaOrigem,
        LocalDate dataUso,
        Periodo turno,
        String motivo,
        StatusForm status,
        OffsetDateTime criadoEm
) {}