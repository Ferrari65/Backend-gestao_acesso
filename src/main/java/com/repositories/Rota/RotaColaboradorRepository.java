package com.repositories.Rota;

import com.domain.user.Rotas.RotaColaborador;
import com.domain.user.Rotas.RotaColaboradorId;
import com.projection.RotaComMaisColaboradoresProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RotaColaboradorRepository extends JpaRepository<RotaColaborador, RotaColaboradorId> {

    Optional<RotaColaborador> findById_IdRotaAndId_IdColaborador(Integer idRota, UUID idColaborador);
    boolean existsByColaborador_IdColaboradorAndId_IdRotaNot(UUID idColaborador, Integer idRota);
    boolean existsByColaborador_IdColaboradorAndRota_IdRota(UUID idColaborador, Integer idRota);
    void deleteById_IdRotaAndId_IdColaborador(Integer idRota, UUID idColaborador);
    List<RotaColaborador> findByRota_IdRota(Integer idRota);
    List<RotaColaborador> findAllById_IdRota(Integer idRota);
    List<RotaColaborador> findByColaborador_IdColaborador(UUID idColaborador);

    Optional<RotaColaborador> findFirstByColaborador_IdColaboradorOrderByDataUsoDesc(UUID idColaborador);

    @Query(value = """
        SELECT 
            r.nome AS nomeRota,
            COUNT(rc.id_colaborador) AS quantidadeColaboradores
        FROM rota_colaborador rc
        JOIN rotas r ON r.id_rota = rc.id_rota
        GROUP BY r.id_rota, r.nome
        ORDER BY quantidadeColaboradores DESC
        LIMIT 1
        """, nativeQuery = true)
    RotaComMaisColaboradoresProjection findRotaComMaisColaboradores();

    @Query("""
        SELECT COUNT(rc)
        FROM RotaColaborador rc
        WHERE UPPER(rc.rota.nome) = UPPER(:nomeRota)
        """)
    long contarColaboradoresPorNomeRota(@Param("nomeRota") String nomeRota);

    @Query(value = """
        SELECT COUNT(*)
        FROM rota_colaborador rc
        JOIN rotas r ON r.id_rota = rc.id_rota
        WHERE UPPER(r.nome) = UPPER(:nomeRota)
          AND r.periodo = :periodo
        """, nativeQuery = true)
    long contarColaboradoresPorNomeRotaEPeriodo(@Param("nomeRota") String nomeRota,
                                                @Param("periodo") String periodo);

    boolean existsByColaborador_IdColaboradorAndId_IdRotaNotAndAtivoTrue(UUID idColaborador, Integer idRota);



    List<RotaColaborador> findByRota_IdRotaAndAtivoTrue(Integer idRota);
    List<RotaColaborador> findByColaborador_IdColaboradorAndAtivoTrue(UUID idColaborador);
    Optional<RotaColaborador> findFirstByColaborador_IdColaboradorAndAtivoTrueOrderByDataUsoDesc(UUID idColaborador);

}
