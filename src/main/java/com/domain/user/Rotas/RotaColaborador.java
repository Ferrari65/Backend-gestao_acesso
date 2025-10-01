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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idColaborador")
    @JoinColumn(name = "id_colaborador", nullable = false)
    private User colaborador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idRota")
    @JoinColumn(name = "id_rota", nullable = false)
    private Rota rota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ponto")
    private Pontos pontos;

    @Column(name = "data_uso", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime dataUso;

    @PrePersist
    public void prePersist() {if (dataUso == null) dataUso = LocalDateTime.now();}

    public RotaColaboradorId getId() {return id;}
    public void setId(RotaColaboradorId id) {this.id = id;}

    public User getColaborador() {return colaborador;}
    public void setColaborador(User colaborador) {this.colaborador = colaborador;}

    public Rota getRota() {return rota;}
    public void setRota(Rota rota) {this.rota = rota;}

    public Pontos getPontos() {return pontos;}
    public void setPontos(Pontos pontos) {this.pontos = pontos;}

    public LocalDateTime getDataUso() {return dataUso;}
    public void setDataUso(LocalDateTime dataUso) {this.dataUso = dataUso;}

}

