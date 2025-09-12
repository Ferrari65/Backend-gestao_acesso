package com.repositories.Colaborador;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;
import com.domain.user.colaborador.ColaboradorForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColaboradorFormRepository extends JpaRepository <ColaboradorForm, UUID> {

    List<ColaboradorForm> findByIdColaborador(UUID idColanorador);
    Optional<ColaboradorForm>findFirstByIdColaboradorAndTurnoAndStatusIn(
            UUID idColaborador, Periodo turno, Collection<StatusForm> status);

    @Query("""
      SELECT cf FROM ColaboradorForm cf
      WHERE (:idCidade IS NULL OR cf.idCidade = :idCidade)
        AND (:turno IS NULL OR cf.turno = :turno)
        AND (:status IS NULL OR cf.status = :status)
      ORDER BY cf.criadoEm DESC
    """)
    List<ColaboradorForm> buscarComFiltros(Integer idCidade, Periodo turno, StatusForm status);
}