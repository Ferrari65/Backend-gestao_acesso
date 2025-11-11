package com.dto.colaborador;

import java.time.LocalDateTime;
import java.util.UUID;

public record RotaColaboradorResponse(
        Integer idRota,
        String nomeRota,
        String nomePonto,
        UUID idColaborador,
        String nomeColaborador,
        String matricula,
        LocalDateTime dataUso,
        Integer idPonto
) {}
