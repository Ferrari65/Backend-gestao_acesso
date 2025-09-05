package com.repositories.localizacao;

import com.domain.user.endereco.Pontos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PontosRepository  extends JpaRepository<Pontos, Integer> {
}
