package com.dto.impedimentos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ImpedimentoDetalhadoResponse(

        // Impedimento
        UUID id,
        String motivo,
        String severidade,
        String descricao,
        BigDecimal latitude,
        BigDecimal longitude,
        OffsetDateTime ocorridoEm,
        boolean ativo,

        // Viagem
        UUID idViagem,
        Integer idRota,
        Integer idMotorista,
        Integer idVeiculo,

        // Relacionados
        String motoristaNome,
        String rotaNome,

        // Colaboradores da rota
        List<ColaboradorItem> colaboradores

) {
    public record ColaboradorItem(
            UUID id,      // se seu User tiver Integer, troque aqui pra Integer
            String nome,
            String matricula
    ) {}
}
