package com.domain.user.Rotas;

import com.domain.user.colaborador.User;
import com.domain.user.endereco.Pontos;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name= "rota_colaborador")
public class RotaColaborador {

    @EmbeddedId
    private RotaColaboradorId id;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @MapsId("idColaborador")
    @JoinColumn(name =  "id_colaborador", nullable = false)
    private User colaborador;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @MapsId("idRota")
    @JoinColumn(name = "id_rota", nullable = false)
    private Rota rota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ponto")
    private Pontos pontos;

    @Column(name = "data_uso", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime dataUso;
}
