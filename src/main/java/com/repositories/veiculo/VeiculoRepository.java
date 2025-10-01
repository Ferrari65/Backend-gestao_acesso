package com.repositories.veiculo;

import com.domain.user.Enum.TipoVeiculo;
import com.domain.user.veiculo.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    List<Veiculo> findByAtivoTrue();
    boolean existsByPlacaIgnoreCaseAndAtivoTrue(String placa);
    List<Veiculo> findByTipoVeiculoAndAtivoTrue(TipoVeiculo tipo);
    Optional<Veiculo> findByIdAndAtivoTrue(Long id);
}
