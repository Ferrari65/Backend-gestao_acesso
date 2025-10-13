package com.repositories.liderRota;

import com.domain.user.LiderRota.LiderRotaId;
import com.domain.user.LiderRota.RotaLider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiderRotaRepository extends JpaRepository<RotaLider, LiderRotaId> {

    List<RotaLider> findByRota_IdRotaAndAtivoTrue(Integer idRota);

    Optional<RotaLider> findByRota_IdRotaAndColaborador_IdColaborador(Integer idRota, UUID idColaborador);
    boolean existsByColaborador_IdColaboradorAndAtivoTrue(UUID idColaborador);
    boolean existsByRota_IdRotaAndColaborador_IdColaboradorAndAtivoTrue(Integer idRota, UUID idColaborador);}