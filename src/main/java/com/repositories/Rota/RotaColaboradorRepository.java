package com.repositories.Rota;

import com.domain.user.Rotas.RotaColaborador;
import com.domain.user.Rotas.RotaColaboradorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RotaColaboradorRepository extends JpaRepository<RotaColaborador, RotaColaboradorId> {

    boolean existsByColaborador_IdColaboradorAndRota_IdRota(UUID idColaborador, Integer idRota);
    Optional<RotaColaborador> findById_IdRotaAndId_IdColaborador(Integer idRota, UUID idColaborador);
    void deleteById_IdRotaAndId_IdColaborador(Integer idRota, UUID idColaborador);
    List<RotaColaborador> findByRota_IdRota(Integer idRota);
    List<RotaColaborador> findByColaborador_IdColaborador(UUID idColaborador);
}
