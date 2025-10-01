package com.repositories.viagem;

import com.domain.user.Enum.TipoViagem;
import com.domain.user.ViagemRota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ViagemRepository extends JpaRepository<ViagemRota, UUID> {

    List<ViagemRota> findByIdRota(Integer idRota);
    List<ViagemRota> findByAtivo(boolean ativo);
}
