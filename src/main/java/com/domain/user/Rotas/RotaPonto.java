package com.domain.user.Rotas;

import com.domain.user.endereco.Pontos;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "rota_pontos")
@Getter
@Setter
public class RotaPonto {

    @EmbeddedId
    private RotaPontoId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idRota")
    @JoinColumn(name = "id_rota", nullable = false)
    private Rota rota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idPonto")
    @JoinColumn(name = "id_ponto", nullable = false)
    private Pontos ponto;

    @Column(nullable = false)
    private Integer ordem;
}
