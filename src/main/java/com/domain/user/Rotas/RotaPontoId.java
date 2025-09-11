package com.domain.user.Rotas;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RotaPontoId implements Serializable {
    private Integer idRota;
    private Integer idPonto;

    public RotaPontoId() {}
    public RotaPontoId(Integer idRota, Integer idPonto) {
        this.idRota = idRota;
        this.idPonto = idPonto;
}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RotaPontoId that = (RotaPontoId) o;
        return Objects.equals(idRota, that.idRota) &&
                Objects.equals(idPonto, that.idPonto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRota, idPonto);
    }
}
