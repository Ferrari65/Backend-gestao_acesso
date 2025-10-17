package com.dto.visitante;

import com.domain.user.Enum.TipoDocumento;

import java.time.LocalDate;
import java.util.UUID;

public record VisitanteResponse(
        UUID id,
        String nomeCompleto,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        LocalDate dataNascimento,
        String telefone,
        String empresaVisitante,
        UUID pessoaAnfitria,
        String motivoVisita,
        boolean ativo
) {}
