package com.domain.user.registroEmbarque;

import com.domain.user.Enum.MetodoValidacao;
import com.domain.user.Enum.StatusEmbarque;
import com.domain.user.viagemRota.ViagemRota;
import com.domain.user.colaborador.ColaboradorForm;
import com.domain.user.colaborador.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "registro_embarque")
public class RegistroEmbarque {

    @Id
    @Column(name = "id_embarque", nullable = false, updatable = false)
    private UUID idEmbarque;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_colaborador", nullable = false)
    private User colaborador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_viagem", nullable = false)
    private ViagemRota viagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_validador")
    private User validador;

    @Column(name = "data_embarque", nullable = false)
    private OffsetDateTime dataEmbarque;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status_embarque", nullable = false, columnDefinition = "status_embarque")
    private StatusEmbarque statusEmbarque;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "metodo_validacao", nullable = false, columnDefinition = "metodo_validacao")
    private MetodoValidacao metodoValidacao;

    @Column(name = "tem_aviso_previo", nullable = false)
    private Boolean temAvisoPrevio = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aviso_previo")
    private ColaboradorForm avisoPrevio;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private OffsetDateTime atualizadoEm;

    @PrePersist
    void pre() {
        if (idEmbarque == null) idEmbarque = UUID.randomUUID();
        if (dataEmbarque == null) dataEmbarque = OffsetDateTime.now();
    }
}
