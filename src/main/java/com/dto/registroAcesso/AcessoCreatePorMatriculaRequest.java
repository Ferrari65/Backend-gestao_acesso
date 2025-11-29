package com.dto.registroAcesso;

import com.domain.user.Enum.TipoPessoa;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AcessoCreatePorMatriculaRequest(
        TipoPessoa tipoPessoa,
        String matriculaOuDocumento,
        Short codPortaria,
        String observacao,

        @JsonProperty("ocupantesMatriculas")
        @JsonAlias({"ocupanteMatriculas", "ocupantes_matriculas"})
        List<String> ocupantesMatriculas,

        @JsonProperty("ocupantesDocumentos")
        @JsonAlias({"ocupanteDocumentos", "ocupantes_documentos"})
        List<String> ocupantesDocumentos
) {}
