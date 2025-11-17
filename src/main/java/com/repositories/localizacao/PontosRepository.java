package com.repositories.localizacao;

import com.domain.user.endereco.Pontos;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PontosRepository  extends JpaRepository<Pontos, Integer> {

    @EntityGraph(attributePaths = {"cidade"})
    List<Pontos> findAll();

    Optional<Pontos> findByNomeIgnoreCase(String nome);

    Optional<Pontos> findByNomeIgnoreCaseAndCidade_IdCidade(String nome, Integer idCidade);

}