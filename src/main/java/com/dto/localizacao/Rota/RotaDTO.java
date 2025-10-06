package com.dto.localizacao.Rota;

import com.domain.user.Enum.Periodo;
import com.domain.user.Rotas.Rota;

import java.time.LocalTime;
import java.util.List;

public record RotaDTO (

    Integer idRota,
    Integer idCidade,
    String cidadeNome,
    String cidadeUf,
    String nome,
    Periodo periodo,
    Integer capacidade,
    Boolean ativo,
    LocalTime horaPartida,
    LocalTime horaChegada,
    List<RotaPontoItemDTO> pontos
) {
    public static RotaDTO from(Rota r) {
        var itens = r.getPontos().stream()
                .map(rp -> new RotaPontoItemDTO(
                        rp.getOrdem(),
                        rp.getPonto().getIdPonto(),
                        rp.getPonto().getNome(),
                        rp.getPonto().getEndereco(),
                        rp.getPonto().getLatitude(),
                        rp.getPonto().getLongitude()
                ))
                .toList();

        return new RotaDTO(
                r.getIdRota(),
                r.getCidade()!=null ? r.getCidade().getIdCidade() : null,
                r.getCidade()!=null ? r.getCidade().getNome() : null,
                r.getCidade()!=null ? r.getCidade().getUf() : null,
                r.getNome(),
                r.getPeriodo(),
                r.getCapacidade(),
                r.getAtivo(),
                r.getHoraPartida(),
                r.getHoraChegada(),
                itens
        );
    }
}