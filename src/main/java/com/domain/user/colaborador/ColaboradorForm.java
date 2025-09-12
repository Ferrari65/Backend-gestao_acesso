package com.domain.user.colaborador;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "colaborador_form")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColaboradorForm {

    @Id
    @GeneratedValue
    @Column(name = "id_form")
    private UUID id;

    @Column(name = "id_colaborador", nullable = false)
    private UUID idColaborador;

    @Column(name = "nome")
    private String nome;

    @Column(name = "codigo")
    private String matricula;

    @Column(name = "endereco_rua")
    private String enderecoRua;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "id_cidade", nullable = false)
    private Integer idCidade;

    @Column(name = "id_ponto")
    private Integer idPonto;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false)
    private Periodo turno;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusForm status = StatusForm.PENDENTE;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;
}
