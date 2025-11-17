package com.domain.user.Enum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Motivo do impedimento")
public enum MotivoImpedimento {
    QUEBRA_ONIBUS,
    ACIDENTE,
    SOCORRO_MEDICO
}
