package com.domain.user.Enum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Severidade do impedimento")
public enum SeveridadeImpedimento {
    BAIXA,
    MEDIA,
    ALTA,
    CRITICA
}
