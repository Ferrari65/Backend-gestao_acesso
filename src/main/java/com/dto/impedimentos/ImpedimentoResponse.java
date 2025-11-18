package com.dto.impedimentos;

import com.domain.user.Enum.MotivoImpedimento;
import com.domain.user.Enum.SeveridadeImpedimento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ImpedimentoResponse(
        UUID id,
        MotivoImpedimento motivo,
        SeveridadeImpedimento severidade,
        BigDecimal latitude,
        BigDecimal longitude,
        String descricao,
        UUID idViagem,
        OffsetDateTime ocorridoEm,
        UUID registradoPor,
        boolean ativo,
        OffsetDateTime tempoFinalizacao
) {
}
