package com.dto.colaborador;

import com.domain.user.Enum.Periodo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record FormCreateRequest(
        Integer idRotaOrigem,
        @NotNull Integer idRotaDestino,
        @NotNull LocalDate dataUso,
        @NotNull Periodo turno,
        @NotNull @Size(min = 10, max = 800) String motivo
) {}
