package com.dto.localizacao.Rota;

import com.domain.user.Enum.Periodo;
import com.domain.user.Rotas.Rota;

import java.time.LocalTime;

public record RotaListDTO(
        Integer idRota,
        Integer idCidade,
        String cidadeNome,
        String cidadeUf,
        String nome,
        Periodo periodo,
        Integer capacidade,
        Boolean ativo,
        LocalTime horaPartida,
        LocalTime horaChegada
) {
    public static RotaListDTO from(Rota r) {
        return new RotaListDTO(
                r.getIdRota(),
                r.getCidade() != null ? r.getCidade().getIdCidade() : null,
                r.getCidade() != null ? r.getCidade().getNome() : null,
                r.getCidade() != null ? r.getCidade().getUf() : null,
                r.getNome(), r.getPeriodo(), r.getCapacidade(), r.getAtivo(),
                r.getHoraPartida(), r.getHoraChegada()
        );
    }
}