package com.dto.veiculo;

import com.domain.user.Enum.TipoVeiculo;

public record VeiculoResponseDTO (

        Long id,
        String placa,
        Integer capacidade,
        TipoVeiculo tipoVeiculo,
        String observacao,
        Boolean ativo

){}
