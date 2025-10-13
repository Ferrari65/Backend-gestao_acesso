package com.services.registroEmbarque;

import com.dto.registroEmbarque.RegistrarEmbarqueRequest;
import com.dto.registroEmbarque.RegistroEmbarqueResponse;

import java.util.UUID;

public interface RegistroEmbarqueService {

    RegistroEmbarqueResponse registrar (UUID idViagem, RegistrarEmbarqueRequest req,UUID idValidador);
}
