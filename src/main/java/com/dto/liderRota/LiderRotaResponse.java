package com.dto.liderRota;

import java.time.LocalDateTime;
import java.util.UUID;

public record LiderRotaResponse(
        Integer idRota,
        UUID idColaborador,
        String nomeLider,
        LocalDateTime dataAtribuicao
) {}