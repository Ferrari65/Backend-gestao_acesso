package com.dto.localizacao.Rota;


import java.math.BigDecimal;

public record RotaPontoItemDTO (
        Integer ordem,
        Integer idPonto,
        String nomePonto,
        BigDecimal latitude,
        BigDecimal  longitude) {}
