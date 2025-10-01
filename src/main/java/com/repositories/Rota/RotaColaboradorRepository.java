package com.repositories.Rota;

import com.domain.user.Rotas.RotaColaborador;
import com.domain.user.Rotas.RotaColaboradorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RotaColaboradorRepository extends JpaRepository<RotaColaborador, RotaColaboradorId> {


    Optional<RotaColaborador> findById_IdRotaAndId_IdColaborador(Integer idRota, UUID idColaborador);
    boolean existsByColaborador_IdColaboradorAndId_IdRotaNot(UUID idColaborador, Integer idRota);
    boolean existsByColaborador_IdColaboradorAndRota_IdRota(UUID idColaborador, Integer idRota);
    void deleteById_IdRotaAndId_IdColaborador(Integer idRota, UUID idColaborador);
    List<RotaColaborador> findByRota_IdRota(Integer idRota);
    List<RotaColaborador> findByColaborador_IdColaborador(UUID idColaborador);
}
