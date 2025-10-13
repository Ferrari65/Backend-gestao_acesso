package com.repositories.registroEmbarque;

import com.domain.user.ViagemRota;
import com.domain.user.colaborador.User;
import com.domain.user.registroEmbarque.RegistroEmbarque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistroEmbarqueRepository extends JpaRepository<RegistroEmbarque, UUID> {
    boolean existsByViagemAndColaborador (ViagemRota viagem, User colaborador);
    Optional<RegistroEmbarque> findByViagemAndColaborador (ViagemRota viagem, User colaborador);
}
