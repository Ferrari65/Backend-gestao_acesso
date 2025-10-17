package com.domain.user.visitante;

import com.domain.user.Enum.TipoDocumento;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "visitantes",
        uniqueConstraints = {
                @UniqueConstraint(name="ux_visitante_doc", columnNames = {"tipo_documento", "numero_documento"})
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitante {

    @Id
    @GeneratedValue
    @Column(name = "id_visitante")
    private UUID id;

    @Column(name = "nome_completo", nullable = false)
    @NotBlank
    private String nomeCompleto;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_documento", nullable=false, columnDefinition = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false)
    @NotBlank
    private String numeroDocumento;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "empresa_visitante")
    private String empresaVisitante;

    @Column(name = "pessoa_anfitria", nullable = false)
    @NotNull
    private UUID pessoaAnfitria;

    @Column(name = "motivo_visita", length = 500)
    private String motivoVisita;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
