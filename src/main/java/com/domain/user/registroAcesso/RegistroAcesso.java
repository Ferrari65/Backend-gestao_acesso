package com.domain.user.registroAcesso;

import com.domain.user.Enum.TipoPessoa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "registro_acesso")
public class RegistroAcesso {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_pessoa", nullable = false, columnDefinition = "tipo_pessoa")
    private TipoPessoa tipoPessoa;

    @Column(name = "id_pessoa", nullable = false)
    private UUID idPessoa;

    @Column(name = "cod_portaria", nullable = false)
    private Short codPortaria;

    @Column(nullable = false)
    private OffsetDateTime entrada;

    private OffsetDateTime saida;

    @Column(length = 255)
    private String observacao;
}
