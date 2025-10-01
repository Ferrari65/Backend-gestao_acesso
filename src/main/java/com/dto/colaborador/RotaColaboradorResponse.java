package com.dto.colaborador;

import java.time.LocalDateTime;
import java.util.UUID;

public record RotaColaboradorResponse(
        Integer idRota,
        UUID idColaborador,
        String nomeColaborador,
        LocalDateTime dataUso,
        Integer idPonto
) {}
