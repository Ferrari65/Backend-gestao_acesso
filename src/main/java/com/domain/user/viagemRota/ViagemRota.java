package com.domain.user.viagemRota;

import com.domain.user.Enum.TipoViagem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "viagens_rota")
public class ViagemRota {

    @Id
    @GeneratedValue
    @Column(name = "id_viagem", updatable = false, nullable = false)
    private UUID idViagem;

    @Column(name = "id_rota", nullable = false)
    private Integer idRota;

    @Column(name = "id_motorista", nullable = false)
    private Integer idMotorista;

    @Column(name = "id_veiculo", nullable = false)
    private Integer idVeiculo;

    @Column(name = "saida_prevista", nullable = false)
    private LocalDate saidaPrevista;

    @Column(name = "chegada_prevista", nullable = false)
    private LocalDate chegadaPrevista;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_viagem", nullable = false, columnDefinition = "tipo_viagem")
    private TipoViagem tipoViagem;

    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime createdAt;

    private OffsetDateTime updated;

    @PrePersist
    void prePersist(){
        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updated = now;
    }

    @PreUpdate
    void preUpdate() { this.updated = OffsetDateTime.now(); }
}
