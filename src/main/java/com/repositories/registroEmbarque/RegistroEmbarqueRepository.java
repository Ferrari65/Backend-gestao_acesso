package com.repositories.registroEmbarque;

import com.domain.user.viagemRota.ViagemRota;
import com.domain.user.colaborador.User;
import com.domain.user.registroEmbarque.RegistroEmbarque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface RegistroEmbarqueRepository extends JpaRepository<RegistroEmbarque, UUID> , JpaSpecificationExecutor<RegistroEmbarque> {
    boolean existsByViagemAndColaborador (ViagemRota viagem, User colaborador);
    Optional<RegistroEmbarque> findByViagemAndColaborador (ViagemRota viagem, User colaborador);



}
