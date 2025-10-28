package com.domain.user.Impedimento;

import com.domain.user.Enum.MotivoImpedimento;
import com.domain.user.Enum.SeveridadeImpedimento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table (name = "impedimento")
public class Impedimento {

    @Id
    @Column(name = "id_impedimento", updatable = false)
    UUID idImpedimento;

    @Enumerated(EnumType.STRING)
    @Column (name = "motivo", columnDefinition = "motivo_impedimento")
    private MotivoImpedimento motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidade",columnDefinition = "severidade_impedimento")
    @Builder.Default
    SeveridadeImpedimento severidade = SeveridadeImpedimento.MEDIA;

    String descricao;

    @Column(name = "id_viagem")
    UUID idViagem;

    @Column( name = "id_veiculo")
    UUID idVeiculo;

    @Column(name = "ocorrido_em")
    OffsetDateTime ocorridoEm;

    @Column(name = "registrado_por")
    private UUID registroPor;

    @Column(name = "ativo")
    @ColumnDefault("true")
    private boolean ativo = true;

    @Column(name = "tempo_finalizacao")
    private OffsetDateTime tempoFinalizacao;

    @PrePersist
    void prePersist() {
        if (idImpedimento == null) idImpedimento = UUID.randomUUID();
    }
}