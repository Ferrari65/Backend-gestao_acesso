package com.dto.colaborador;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FormResponse(
        UUID idForm,
        UUID idColaborador,
        String nome,
        String codigo,
        String enderecoRua,
        String bairro,
        Integer idCidade,
        Integer idPonto,
        Periodo turno,
        StatusForm status,
        OffsetDateTime criadoEm
) {}