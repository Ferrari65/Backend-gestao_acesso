package com.dto.visitante;

import com.domain.user.Enum.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record VisitanteCreateRequest(
        @NotBlank
        String nomeCompleto,

        @NotNull
        TipoDocumento tipoDocumento,

        @NotBlank
        String numeroDocumento,

        LocalDate dataNascimento,
        String telefone,
        String empresaVisitante,

        @NotNull
        UUID pessoaAnfitria,

        @Size(max = 500) String motivoVisita,
        Boolean ativo
) {}