package com.repositories.Motorista;

import com.domain.user.motorista.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    List<Motorista> findByAtivoTrue();
}
