package com.services.colaborador;

import com.dto.colaborador.ColaboradorDTO;
import com.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorService {
    private final UserRepository userRepository;

    public List<ColaboradorDTO> listar() {
        return userRepository.findAllWithCidadeAndRole()
                .stream()
                .map(ColaboradorDTO::from)
                .toList();
    }
}
