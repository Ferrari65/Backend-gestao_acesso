package com.services.registroEmbarque;

import com.dto.registroEmbarque.RegistrarEmbarqueRequest;
import com.dto.registroEmbarque.RegistroEmbarqueResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface RegistroEmbarqueService {

    RegistroEmbarqueResponse registrar (UUID idViagem, RegistrarEmbarqueRequest req,UUID idValidador);
    List<RegistroEmbarqueResponse> listarTodos(UUID idViagem);
    String montarMensagemEmbarquesInvalidosSemanaAtual();


}
