package com.repositories.Rota;

import com.domain.user.Rotas.RotaPonto;
import com.domain.user.Rotas.RotaPontoId;
import com.dto.localizacao.Rota.RotaPontoItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RotaPontoRepository extends JpaRepository<RotaPonto, RotaPontoId> {

    List<RotaPonto> findByRota_IdRotaOrderByOrdemAsc(Integer idRota);
    Optional<RotaPonto> findFirstByRota_IdRotaOrderByOrdemAsc(Integer idRota);
    boolean existsByRota_IdRotaAndPonto_IdPontoAndOrdem(Integer idRota, Integer idPonto, Integer ordem);

    boolean existsByRota_IdRota(Integer idRota);
    @Query("""
           select new com.dto.localizacao.Rota.RotaPontoItemDTO(
             rp.ordem, p.idPonto, p.nome, p.latitude, p.longitude)
           from RotaPonto rp join rp.ponto p
           where rp.rota.idRota = :idRota
           order by rp.ordem asc
           """)
    List<RotaPontoItemDTO> listarPontosDTO(@Param("idRota") Integer idRota);
}
