package com.dto.registroAcesso;

import com.domain.user.Enum.TipoPessoa;

import java.util.List;
import java.util.UUID;

public record AcessoCreateRequest(
        TipoPessoa tipoPessoa,
        UUID idPessoa,
        Integer codPortaria,
        String observacao,
        List<UUID> ocupantes
) {}
