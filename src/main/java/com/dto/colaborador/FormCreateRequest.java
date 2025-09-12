package com.dto.colaborador;

import com.domain.user.Enum.Periodo;

public record FormCreateRequest(
        String nome,
        String codigo,
        String enderecoRua,
        String bairro,
        Integer idCidade,
        Integer idPonto,
        Periodo turno
) {}