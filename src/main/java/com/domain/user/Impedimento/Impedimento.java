package com.domain.user.Impedimento;

import com.domain.user.Enum.MotivoImpedimento;
import com.domain.user.Enum.SeveridadeImpedimento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "impedimento")
public class Impedimento {

    @Id
    @Column(name = "id_impedimento", updatable = false, nullable = false)
    private UUID idImpedimento;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "motivo", nullable = false, columnDefinition = "motivo_impedimento")
    private MotivoImpedimento motivo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "severidade", nullable = false, columnDefinition = "severidade")
    @Builder.Default
    private SeveridadeImpedimento severidade = SeveridadeImpedimento.MEDIA;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "id_viagem", nullable = false)
    private UUID idViagem;

    @Column(name = "ocorrido_em", nullable = false)
    private OffsetDateTime ocorridoEm;

    @Column(name = "registrado_por", nullable = false)
    private UUID registradoPor;

    @Column(name = "ativo", nullable = false)
    @ColumnDefault("true")
    @Builder.Default
    private boolean ativo = true;

    @Column(name = "tempo_finalizacao")
    private OffsetDateTime tempoFinalizacao;

    @PrePersist
    void prePersist() {
        if (idImpedimento == null) {
            idImpedimento = UUID.randomUUID();
        }
        if (ocorridoEm == null) {
            ocorridoEm = OffsetDateTime.now();
        }
    }
}