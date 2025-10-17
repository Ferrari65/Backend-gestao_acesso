package com.domain.user.viagemRota;

import com.domain.user.Enum.TipoViagem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Column(nullable = false) // dia da viagem
    private LocalDate data;

    @Column(name = "id_rota", nullable = false)
    private Integer idRota;

    @Column(name = "id_motorista", nullable = false)
    private Integer idMotorista;

    @Column(name = "id_veiculo", nullable = false)
    private Integer idVeiculo;

    @Column(name = "saida_prevista", nullable = false)
    private LocalTime saidaPrevista;

    @Column(name = "chegada_prevista", nullable = false)
    private LocalTime chegadaPrevista;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_viagem", nullable = false, columnDefinition = "tipo_viagem")
    private TipoViagem tipoViagem;

    @Column(nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime createdAt;

    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime updated;

    @PrePersist
    void prePersist() {
        this.updated = OffsetDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updated = OffsetDateTime.now();
    }
}
