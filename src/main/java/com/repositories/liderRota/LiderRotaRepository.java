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
        INSERT INTO lider_rota (id_colaborador, id_rota, data_atribuicao, ativo, data_inativacao)
        VALUES (:idColaborador, :idRota, now(), TRUE, NULL)
        ON CONFLICT (id_colaborador, id_rota)
        DO UPDATE SET 
            ativo = TRUE,
            data_inativacao = NULL
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

    @Query(value = """
        SELECT id_colaborador 
        FROM lider_rota 
        WHERE id_rota = :idRota AND ativo = TRUE
        """, nativeQuery = true)
    List<UUID> findLideresDaRota(@Param("idRota") Integer idRota);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE lider_rota
        SET ativo = FALSE,
            data_inativacao = now()
        WHERE id_rota = :idRota 
          AND id_colaborador = :idColaborador
          AND ativo = TRUE
        """, nativeQuery = true)
    void inativarLider(@Param("idRota") Integer idRota, @Param("idColaborador") UUID idColaborador);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1 FROM lider_rota 
            WHERE id_colaborador = :idColaborador
              AND ativo = TRUE
        )
        """, nativeQuery = true)
    boolean isLiderEmAlgumaRota(@Param("idColaborador") UUID idColaborador);
}
