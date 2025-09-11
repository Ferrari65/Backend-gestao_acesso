package com.repositories.Rota;

import com.domain.user.Rotas.RotaPonto;
import com.domain.user.Rotas.RotaPontoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RotaPontoRepository extends JpaRepository<RotaPonto, RotaPontoId> {

    List<RotaPonto> findByRota_IdRotaOrderByOrdemAsc(Integer idRota);

}
