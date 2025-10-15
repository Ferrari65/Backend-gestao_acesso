package com.domain.user.Rotas;

import com.domain.user.Enum.Periodo;
import com.domain.user.endereco.Cidade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rotas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "idRota")
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rota")
    private Integer idRota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cidade", nullable = false)
    private Cidade cidade;

    private String nome;

    @NotNull(message = "O período é obrigatório")
    @Enumerated(EnumType.STRING)
    private Periodo periodo;

    private Integer capacidade;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "hora_partida")
    private LocalTime horaPartida;

    @Column(name = "hora_chegada")
    private LocalTime horaChegada;

    @OneToMany(mappedBy = "rota", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<RotaPonto> pontos = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void prePersistUpdate() {
        normalizarNomeHelper();
        ajustarHoraParaMinutosHelper();
    }

    private void normalizarNomeHelper() {
        if (nome != null) {
            nome = nome.trim().toUpperCase();
        }
    }

    private void ajustarHoraParaMinutosHelper() {
        if (horaPartida != null) {
            horaPartida = horaPartida.truncatedTo(ChronoUnit.MINUTES);
        }
        if (horaChegada != null) {
            horaChegada = horaChegada.truncatedTo(ChronoUnit.MINUTES);
        }
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = (periodo != null) ? Periodo.valueOf(periodo.name().toUpperCase()) : null;
    }
}
