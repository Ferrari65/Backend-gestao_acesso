package com.dto.localizacao.Rota;


import java.math.BigDecimal;

public record RotaPontoItemDTO (
        Integer ordem,
        Integer idPonto,
        String nomePonto,
        String endereco,
        BigDecimal latitude,
        BigDecimal  longitude) {}
