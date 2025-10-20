package com.dto.registroAcesso;

import com.domain.user.Enum.TipoPessoa;

import java.util.List;

public record AcessoCreatePorMatriculaRequest(
        TipoPessoa tipoPessoa,
        String matriculaOuDocumento,
        Short codPortaria,
        String observacao,
        List<String> ocupantesMatriculas
) {}
