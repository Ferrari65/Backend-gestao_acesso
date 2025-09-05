package com.dto.localizacao;

import java.math.BigDecimal;

public record PontosRequestDTO(
    Integer idCidade,
    String nome,
    String endereco,
    BigDecimal latitude,
    BigDecimal longitude
){}
