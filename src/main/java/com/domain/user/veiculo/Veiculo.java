package com.domain.user.veiculo;


import com.domain.user.Enum.TipoVeiculo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "veiculos")
@Getter
@Setter
@NoArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veiculo")
    private Long id;

    @Column(length = 10)
    private String placa;

    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column (name = "tipo_veiculo")
    private TipoVeiculo tipoVeiculo;

    private String observacao;

    @Column(nullable = false)
    private Boolean ativo = true;
}
