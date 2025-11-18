package com.dto.impedimentos;

import com.domain.user.Enum.MotivoImpedimento;
import com.domain.user.Enum.SeveridadeImpedimento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ImpedimentoCreateRequest (

        @NotNull
        @Schema(description = "Motivo do impedimento")
        MotivoImpedimento motivo,

        @NotNull
        @Schema(description = "Severidade do impedimento")
        SeveridadeImpedimento severidade,

        BigDecimal latitude,
        BigDecimal longitude,
        String descricao,
        UUID idViagem,
        UUID registradoPor

)
{ }
