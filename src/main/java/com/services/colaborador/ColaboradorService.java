package com.services.colaborador;

import com.dto.colaborador.ColaboradorDTO;
import com.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final UserRepository userRepository;

    public List<ColaboradorDTO> listar() {
        return userRepository.findAll()
                .stream()
                .map(ColaboradorDTO::from)
                .toList();
    }

    public ColaboradorDTO buscarPorId(UUID idColaborador) {
        var user = userRepository.findById(idColaborador)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado: " + idColaborador));
        return ColaboradorDTO.from(user);
    }
}
