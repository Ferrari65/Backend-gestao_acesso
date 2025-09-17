package com.domain.user.colaborador;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "colaborador_form")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ColaboradorForm {

    @Id
    @GeneratedValue
    @Column(name = "id_form")
    private UUID id;

    @Column(name = "id_colaborador", nullable = false)
    private UUID idColaborador;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "codigo", nullable = false)
    private String matricula;

    @Column(name = "id_rota_origem")
    private Integer idRotaOrigem;

    @Column(name = "id_rota_destino", nullable = false)
    private Integer idRotaDestino;

    @Column(name = "data_uso", nullable = false)
    private LocalDate dataUso;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false)
    private Periodo turno;

    @Column(name = "motivo", nullable = false, columnDefinition = "text")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusForm status = StatusForm.PENDENTE;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC);
        if (status == null) status = StatusForm.PENDENTE;
    }
}
