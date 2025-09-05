package com.repositories.localizacao;

import com.domain.user.endereco.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CidadeRepository  extends JpaRepository<Cidade, Integer> {
    boolean existsByNomeIgnoreCaseAndUfIgnoreCase(String nome, String uf);
    boolean existsByNomeIgnoreCaseAndUfIgnoreCaseAndIdCidadeNot(String nome, String uf, Integer idCidade);
    List<Cidade> findAllByUfOrderByNomeAsc(String uf);
}
