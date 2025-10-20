package com.dto.registroAcesso;

import com.domain.user.Enum.TipoPessoa;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AcessoResponse(
        UUID id,
        TipoPessoa tipoPessoa,
        PessoaMinDTO condutor,          // id + nome (nome só para exibição)
        Short codPortaria,
        OffsetDateTime entrada,
        OffsetDateTime saida,
        String observacao,
        List<PessoaMinDTO> ocupantes
) {}
