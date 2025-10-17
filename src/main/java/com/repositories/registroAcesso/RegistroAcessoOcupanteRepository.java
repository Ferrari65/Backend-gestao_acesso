package com.repositories.registroAcesso;

import com.domain.user.registroAcesso.RegistroAcessoOcupante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegistroAcessoOcupanteRepository extends JpaRepository<RegistroAcessoOcupante, UUID> {
    List<RegistroAcessoOcupante> findByRegistroId(UUID idRegistro);
}