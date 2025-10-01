package com.repositories.Colaborador;

import com.domain.user.Enum.StatusForm;
import com.domain.user.colaborador.ColaboradorForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColaboradorFormRepository extends JpaRepository<ColaboradorForm, UUID> {
    Optional<ColaboradorForm> findFirstByIdColaboradorAndDataUsoAndIdRotaDestinoAndStatusIn(
            UUID idColaborador, LocalDate dataUso, Integer idRotaDestino, Collection<StatusForm> statuses
    );

    List<ColaboradorForm> findByIdColaboradorOrderByCriadoEmDesc(UUID idColaborador);
    List<ColaboradorForm> findByIdColaboradorAndStatusOrderByCriadoEmDesc(UUID idColaborador, StatusForm status);

    List<ColaboradorForm> findAllByOrderByCriadoEmDesc();
    List<ColaboradorForm> findByStatusOrderByCriadoEmDesc(StatusForm status);
}
