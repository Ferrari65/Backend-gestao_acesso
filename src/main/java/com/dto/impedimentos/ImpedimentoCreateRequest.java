package com.dto.impedimentos;

import com.domain.user.Enum.MotivoImpedimento;
import com.domain.user.Enum.SeveridadeImpedimento;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ImpedimentoCreateRequest (
        MotivoImpedimento motivo,
        SeveridadeImpedimento severidade,
        String descricao,
        UUID idViagem,
        UUID idVeiculo,
        OffsetDateTime ocorridoEm,
        UUID registradoPor

)
{ }
