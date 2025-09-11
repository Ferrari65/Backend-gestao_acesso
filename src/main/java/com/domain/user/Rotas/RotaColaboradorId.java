package com.domain.user.Rotas;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RotaColaboradorId  implements Serializable {

    private UUID idColaborador;
    private Integer idRota;

    public RotaColaboradorId(){}
    public RotaColaboradorId(UUID idColaborador, Integer idRota){
    }

    @Override
    public boolean equals(Object o){
        if (this == o ) return  true;
        if (!(o instanceof RotaColaboradorId that)) return  false;
        return Objects.equals(idColaborador, that.idColaborador) &&
                Objects.equals(idRota, that.idRota);
    }

    @Override
    public int hashCode(){
        return Objects.hash(idColaborador, idRota);
    }
}
