package com.dto.veiculo;

import com.domain.user.Enum.TipoVeiculo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VeiculoRequestDTO (

    String placa,

    @Positive(message = "capacidade deve ser > 0")
    Integer capacidade,

    @NotNull(message = "tipoVeiculo não pde estar VAZIO")
    TipoVeiculo tipoVeiculo,

    @Size(max = 255)
    String observacao
){}
