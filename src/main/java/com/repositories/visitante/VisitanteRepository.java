package com.repositories.visitante;

import com.domain.user.Enum.TipoDocumento;
import com.domain.user.visitante.Visitante;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitanteRepository extends JpaRepository<Visitante, UUID> {
    boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
    List<Visitante> findAllByAtivo(boolean ativo, Sort sort);
    Optional<Visitante> findByDocumento(String documento);
    Optional<Visitante> findByIdAndAtivoTrue(UUID id);
}
