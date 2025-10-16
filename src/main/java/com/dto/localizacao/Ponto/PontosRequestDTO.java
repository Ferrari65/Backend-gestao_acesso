package com.dto.localizacao.Ponto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.infra.config.BigDecimalCommaDeserializer;

import java.math.BigDecimal;

public record PontosRequestDTO(
        Integer idCidade,
        String nome,
        String endereco,
        @JsonDeserialize(using = BigDecimalCommaDeserializer.class)
        BigDecimal latitude,
        @JsonDeserialize(using = BigDecimalCommaDeserializer.class)
        BigDecimal longitude
) {}
