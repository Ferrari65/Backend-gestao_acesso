package com.domain.user.LiderRota;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class LiderRotaId {
    private UUID idColaborador;
    private Integer idRota;

    public LiderRotaId(){}
    public LiderRotaId(UUID idColaborador, Integer idRota){
        this.idColaborador = idColaborador;
        this.idRota = idRota;
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LiderRotaId that)) return false;
        return Objects.equals(idColaborador, that.idColaborador) &&
                Objects.equals(idRota, that.idRota);
    }
    @Override public int hashCode() {
        return Objects.hash(idColaborador, idRota);
    }

}
