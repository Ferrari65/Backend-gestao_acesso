package com.dto.mapa;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MapaColabPontoDTO {

    private Integer idPonto;
    private String nome;
    private BigDecimal  latitude;
    private BigDecimal  longitude;
    private Long quantidadeColaboradores;

    public MapaColabPontoDTO(
            Integer idPonto,
            String nome,
            BigDecimal  latitude,
            BigDecimal  longitude,
            Long quantidadeColaboradores
    ) {
        this.idPonto = idPonto;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantidadeColaboradores = quantidadeColaboradores;
    }
}