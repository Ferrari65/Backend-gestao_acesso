package com.domain.user.LiderRota;

import com.domain.user.Rotas.Rota;
import com.domain.user.colaborador.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lider_rota")
@Getter
@Setter
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

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "data_inativacao")
    private LocalDateTime dataInativacao;

    @PrePersist
    public void prePersist() {if (dataAtribuicao == null) {dataAtribuicao = LocalDateTime.now();}
        if (dataInativacao != null) {ativo = false;}
    }

}
