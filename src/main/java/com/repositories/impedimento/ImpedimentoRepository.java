package com.repositories.impedimento;

import com.domain.user.Enum.SeveridadeImpedimento;
import com.domain.user.Impedimento.Impedimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImpedimentoRepository extends JpaRepository<Impedimento, UUID> {
    List<Impedimento> findByAtivoTrue();
    List<Impedimento> findByIdViagem(UUID idViagem);
    List<Impedimento> findBySeveridade(SeveridadeImpedimento severidade);
    List<Impedimento> findBySeveridadeAndAtivoTrue(SeveridadeImpedimento severidade);
}
