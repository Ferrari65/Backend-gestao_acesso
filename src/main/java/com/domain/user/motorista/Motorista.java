package com.domain.user.motorista;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Table(name = "motorista")
@Getter
@Setter
@NoArgsConstructor
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_motorista")
    private Long id;

    private String nome;
    private String cnh;
    private String telefone;

    @Column(name = "empresa_terceiro")
    private String empresaTerceiro;

    @Column(name = "data_venc_cnh")
    private LocalDate dataVencCnh;

    private Boolean ativo = true;


}
