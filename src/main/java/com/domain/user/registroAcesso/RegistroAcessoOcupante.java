package com.domain.user.registroAcesso;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "registro_acesso_ocupante",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ocupante_por_registro",
                columnNames = {"id_registro", "id_colaborador"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAcessoOcupante {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_registro", nullable = false)
    private RegistroAcesso registro;

    @Column(name = "id_colaborador", nullable = false)
    private UUID idPessoaOcupante;
}
