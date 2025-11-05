package com.dto.localizacao.Rota;

import java.util.UUID;

public record RotaComMaisColaboradoresDTO(
        String nomeRota,
        Long quantidadeColaboradores
) {}