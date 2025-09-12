package com.domain.user.LiderRota;

import com.domain.user.Rotas.Rota;
import com.domain.user.colaborador.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lider_rota")
public class RotaLider {

    @EmbeddedId
    private LiderRotaId id;

    @MapsId("idColaborador")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colaborador", nullable = false)
    private User colaborador;

    @MapsId("idRota")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_rota", nullable = false)
    private Rota rota;

    @Column(name = "data_atribuicao")
    private LocalDateTime dataAtribuicao;

}
