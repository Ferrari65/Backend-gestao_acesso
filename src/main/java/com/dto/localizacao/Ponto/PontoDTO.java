package com.dto.localizacao.Ponto;

import com.domain.user.endereco.Pontos;

import java.math.BigDecimal;

public record PontoDTO (
        Integer idPonto,
        Integer idCidade,
        String  nome,
        BigDecimal latitude,
        BigDecimal longitude
){
    public static PontoDTO from (Pontos p){
        return new PontoDTO(
                p.getIdPonto(),
                p.getCidade() != null ? p.getCidade().getIdCidade() : null,
                p.getNome(),
                p.getLatitude(),
                p.getLongitude()
        );
    }
}
