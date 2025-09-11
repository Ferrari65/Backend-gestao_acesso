package com.repositories.Rota;

import com.domain.user.Enum.Periodo;
import com.domain.user.Rotas.Rota;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
