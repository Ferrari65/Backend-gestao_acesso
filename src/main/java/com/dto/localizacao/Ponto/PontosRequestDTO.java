package com.dto.localizacao.Ponto;

import java.math.BigDecimal;

public record PontosRequestDTO(
    Integer idCidade,
    String nome,
    String endereco,
    String periodo,
    BigDecimal latitude,
    BigDecimal longitude
){}
