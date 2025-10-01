package com.repositories.Rota;

import com.domain.user.Enum.Periodo;
import com.domain.user.Rotas.Rota;
import com.domain.user.endereco.Cidade;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RotaRepository extends JpaRepository<Rota, Integer> {

    boolean existsByCidade_IdCidadeAndNomeIgnoreCaseAndPeriodo(
            Integer idCidade, String nome, Periodo periodo);

    boolean existsByCidade_IdCidadeAndNomeIgnoreCaseAndPeriodoAndIdRotaNot(
            Integer idCidade, String nome, Periodo periodo, Integer idRota);

    @EntityGraph(attributePaths = {"cidade", "pontos", "pontos.ponto"})
    List<Rota> findAll();

    @EntityGraph(attributePaths = {"cidade", "pontos", "pontos.ponto"})
    Optional<Rota> findById(Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Rota r set r.nome = upper(:nome) where r.idRota = :id")
    int updateNome(@Param("id") Integer id, @Param("nome") String nome);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Rota r set r.periodo = :periodo where r.idRota = :id")
    int updatePeriodo(@Param("id") Integer id, @Param("periodo") Periodo periodo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Rota r set r.capacidade = :capacidade where r.idRota = :id")
    int updateCapacidade(@Param("id") Integer id, @Param("capacidade") Integer capacidade);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Rota r set r.ativo = :ativo where r.idRota = :id")
    int updateAtivo(@Param("id") Integer id, @Param("ativo") Boolean ativo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Rota r set r.horaPartida = :hora where r.idRota = :id")
    int updateHoraPartida(@Param("id") Integer id, @Param("hora") LocalTime hora);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Rota r set r.horaChegada = :hora where r.idRota = :id")
    int updateHoraChegada(@Param("id") Integer id, @Param("hora") LocalTime hora);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Rota r set r.cidade = :cidade where r.idRota = :id")
    int updateCidade(@Param("id") Integer id, @Param("cidade") Cidade cidade);
}
