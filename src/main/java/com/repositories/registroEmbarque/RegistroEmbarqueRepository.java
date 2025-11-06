package com.repositories.registroEmbarque;

import com.domain.user.Enum.Periodo;
import com.domain.user.viagemRota.ViagemRota;
import com.domain.user.colaborador.User;
import com.domain.user.registroEmbarque.RegistroEmbarque;
import com.projection.RotaComMaisEmbarquesHojeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroEmbarqueRepository extends JpaRepository<RegistroEmbarque, UUID>, JpaSpecificationExecutor<RegistroEmbarque> {

    boolean existsByViagemAndColaborador(ViagemRota viagem, User colaborador);

    Optional<RegistroEmbarque> findByViagemAndColaborador(ViagemRota viagem, User colaborador);
    boolean existsByViagemAndColaboradorAndDataEmbarqueBetween(
            ViagemRota viagem,
            User colaborador,
            OffsetDateTime inicio,
            OffsetDateTime fim
    );

    @Query(value = """
        SELECT 
            r.nome AS nomeRota,
            COUNT(*) AS totalEmbarques
        FROM registro_embarque re
        JOIN viagens_rota v ON v.id_viagem = re.id_viagem
        JOIN rotas r ON r.id_rota = v.id_rota
        WHERE re.data_embarque::date = :data
        GROUP BY r.id_rota, r.nome
        ORDER BY totalEmbarques DESC
        LIMIT 1
        """, nativeQuery = true)
    RotaComMaisEmbarquesHojeProjection findRotaComMaisEmbarquesNaData(@Param("data") LocalDate data);

    @Query("""
        SELECT COUNT(re)
        FROM RegistroEmbarque re
        JOIN re.viagem v
        JOIN re.colaborador c
        WHERE re.temAvisoPrevio = false
          AND re.statusEmbarque = com.domain.user.Enum.StatusEmbarque.NAO_EMBARCOU
          AND re.dataEmbarque >= :inicio
          AND re.dataEmbarque < :fim
          AND NOT EXISTS (
              SELECT 1
              FROM com.domain.user.Rotas.RotaColaborador rc
              WHERE rc.colaborador = c
                AND rc.rota.idRota = v.idRota
          )
        """)
    long contarEmbarquesInvalidosPorPeriodo(@Param("inicio") OffsetDateTime inicio,
                                            @Param("fim") OffsetDateTime fim);

    @Query("""
        SELECT c
        FROM RotaColaborador rc
            JOIN rc.colaborador c
            JOIN rc.rota r
        WHERE r.idRota = :idRota
          AND NOT EXISTS (
              SELECT 1
              FROM RegistroEmbarque re
              WHERE re.colaborador = c
                AND re.dataEmbarque >= :inicioDia
                AND re.dataEmbarque < :fimDia
          )
        ORDER BY c.nome
        """)
    List<User> buscarNaoEmbarcadosNaRotaHoje(
            @Param("idRota") Integer idRota,
            @Param("inicioDia") OffsetDateTime inicioDia,
            @Param("fimDia") OffsetDateTime fimDia
    );

    @Query("""
        SELECT c
        FROM RotaColaborador rc
            JOIN rc.colaborador c
            JOIN rc.rota r
            JOIN r.cidade cid
        WHERE UPPER(r.nome) = UPPER(:nome)
          AND r.periodo = :periodo
          AND cid.idCidade = :idCidade
          AND NOT EXISTS (
              SELECT 1
              FROM RegistroEmbarque re
              WHERE re.colaborador = c
                AND re.dataEmbarque >= :inicioDia
                AND re.dataEmbarque < :fimDia
          )
        ORDER BY c.nome
        """)
    List<User> buscarNaoEmbarcadosHojePorRotaPeriodoCidade(
            @Param("nome") String nomeRota,
            @Param("periodo") Periodo periodo,
            @Param("idCidade") Integer idCidade,
            @Param("inicioDia") OffsetDateTime inicioDia,
            @Param("fimDia") OffsetDateTime fimDia
    );
}
