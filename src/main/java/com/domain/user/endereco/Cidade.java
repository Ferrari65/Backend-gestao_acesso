package com.domain.user.endereco;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idCidade")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cidade")
    private Integer idCidade;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 2)
    private String uf;

    @PrePersist
    @PreUpdate
    public void normalizarCampos(){
        if(nome!=null){
            nome = nome.trim().toUpperCase();
        }
        if (uf != null) {
            uf = uf.trim().toUpperCase();
        }
    }
}