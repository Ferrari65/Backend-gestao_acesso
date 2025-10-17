package com.dto.PATCH;

import jakarta.validation.constraints.NotNull;

public record VisitanteAtivoPatchRequest(
        @NotNull Boolean ativo
) {}
