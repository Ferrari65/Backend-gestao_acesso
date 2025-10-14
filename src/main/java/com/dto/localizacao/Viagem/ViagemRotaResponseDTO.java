package com.dto.localizacao.Viagem;

import com.domain.user.Enum.TipoViagem;
import com.domain.user.viagemRota.ViagemRota;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ViagemRotaResponseDTO(
        UUID idViagem,
        Integer idRota,
        Integer idMotorista,
        Integer idVeiculo,
        LocalDate saidaPrevista,
        LocalDate chegadaPrevista,
        TipoViagem tipoViagem,
        boolean ativo,
        OffsetDateTime createdAt,
        OffsetDateTime updated
) {
    public static ViagemRotaResponseDTO fromEntity (ViagemRota v) {
        return new ViagemRotaResponseDTO(
                v.getIdViagem(),
                v.getIdRota(),
                v.getIdMotorista(),
                v.getIdVeiculo(),
                v.getSaidaPrevista(),
                v.getChegadaPrevista(),
                v.getTipoViagem(),
                v.isAtivo(),
                v.getCreatedAt(),
                v.getUpdated()
        );
    }
}
