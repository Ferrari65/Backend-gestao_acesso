package com.domain.user.Role;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role {

    @Id
    @Column(name = "id_role")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nome;

    public RoleName getRoleName() {
        return RoleName.from(this.nome);
    }

    @PrePersist @PreUpdate
    private void normalize() {
        if (this.nome != null) this.nome = this.nome.trim().toUpperCase();
    }
}
