package com.dto.localizacao.Viagem;

import com.domain.user.Enum.TipoViagem;
import com.domain.user.ViagemRota;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ViagemRotaResponseDTO(
        UUID idViagem,
        Integer idRota,
        Integer idMotorista,
        Integer idVeiculo,
        LocalDate saidaPrecista,
        LocalDate chegadaPrevista,
        TipoViagem tipoViagem,
        OffsetDateTime createdAt
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
                v.getCreatedAt()
        );
    }
}
