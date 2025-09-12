package com.repositories.liderRota;

import com.domain.user.LiderRota.LiderRotaId;
import com.domain.user.LiderRota.RotaLider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LiderRotaRepository extends JpaRepository<RotaLider, LiderRotaId> {


    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO lider_rota (id_colaborador, id_rota, data_atribuicao)
        VALUES (:idColaborador, :idRota, now())
        ON CONFLICT (id_colaborador, id_rota) DO NOTHING
        """, nativeQuery = true)
    void addLider(@Param("idRota") Integer idRota,
                  @Param("idColaborador") UUID idColaborador);

    @Query(value = """
        SELECT data_atribuicao 
        FROM lider_rota 
        WHERE id_rota = :idRota AND id_colaborador = :idColaborador
        """, nativeQuery = true)
    LocalDateTime findDataAtribuicao(@Param("idRota") Integer idRota,
                                     @Param("idColaborador") UUID idColaborador);

    @Query(value = "SELECT id_colaborador FROM lider_rota WHERE id_rota = :idRota", nativeQuery = true)
    List<UUID> findLideresDaRota(@Param("idRota") Integer idRota);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lider_rota WHERE id_rota = :idRota AND id_colaborador = :idColaborador", nativeQuery = true)
    void deleteUmLider(@Param("idRota") Integer idRota, @Param("idColaborador") UUID idColaborador);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM lider_rota WHERE id_colaborador = :idColaborador)", nativeQuery = true)
    boolean isLiderEmAlgumaRota(@Param("idColaborador") UUID idColaborador);
}
