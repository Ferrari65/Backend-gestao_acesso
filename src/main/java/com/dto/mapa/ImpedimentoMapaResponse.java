package com.dto.mapa;

import com.domain.user.Enum.MotivoImpedimento;
import com.domain.user.Enum.SeveridadeImpedimento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ImpedimentoMapaResponse(
        UUID id,
        BigDecimal latitude,
        BigDecimal longitude,
        SeveridadeImpedimento severidade,
        MotivoImpedimento motivo,
        String descricao,
        OffsetDateTime ocorridoEm,
        boolean ativo
) {
}
