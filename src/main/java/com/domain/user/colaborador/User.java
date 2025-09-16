package com.domain.user.colaborador;

import com.domain.user.Role.Role;
import com.domain.user.endereco.Cidade;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "colaboradores")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idColaborador")
@ToString(exclude = "senha")

public class User implements UserDetails {
    @Id
    @Column(name = "id_colaborador")
    private UUID idColaborador;

    @ManyToOne
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;

    private String nome;
    private String matricula;
    private String cpf;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;


    private LocalDate dataNasc;
    private String    logradouro;
    private String    bairro;
    private Integer   numero;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = (role != null && role.getRoleName() != null)
                ? role.getRoleName().name()
                : "COLABORADOR";

        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }


    @Override public String getPassword()               { return senha; }
    @Override public String getUsername() { return email != null ? email : matricula; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return ativo; }

}
