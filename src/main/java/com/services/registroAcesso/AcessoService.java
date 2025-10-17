package com.services.registroAcesso;

import com.dto.registroAcesso.AcessoCreateRequest;
import com.dto.registroAcesso.AcessoResponse;
import com.repositories.UserRepository;
import com.repositories.registroAcesso.RegistroAcessoOcupanteRepository;
import com.repositories.registroAcesso.RegistroAcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AcessoService {

    private static final Set<Integer> PORTARIAS_VALIDAS = Set.of(1, 2, 3, 4, 5);

    private final RegistroAcessoRepository registroRepo;
    private final RegistroAcessoOcupanteRepository ocupanteRepo;
    private final UserRepository userRepo;

    @Transactional
    public AcessoResponse criar (AcessoCreateRequest req){}
}
