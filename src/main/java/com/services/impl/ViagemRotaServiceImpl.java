// src/main/java/com/services/impl/ViagemRotaServiceImpl.java
package com.services.impl;

import com.domain.user.ViagemRota;
import com.dto.localizacao.Viagem.ViagemRotaRequestDTO;
import com.dto.localizacao.Viagem.ViagemRotaResponseDTO;
import com.repositories.viagem.ViagemRepository;
import com.services.ViagemRotaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViagemRotaServiceImpl implements ViagemRotaService {

    private final ViagemRepository viagemRepository;

    @Override
    public ViagemRotaResponseDTO criar(ViagemRotaRequestDTO dto) {

        if (dto.chegadaPrevista().isBefore(dto.saidaPrevista())) {
            throw new IllegalArgumentException("chegadaPrevista não pode ser antes de saidaPrevista");
        }

        ViagemRota v = ViagemRota.builder()
                .idRota(dto.idRota())
                .idMotorista(dto.idMotorista())
                .idVeiculo(dto.idVeiculo())
                .saidaPrevista(dto.saidaPrevista())
                .chegadaPrevista(dto.chegadaPrevista())
                .tipoViagem(dto.tipoViagem())
                .build();

        v = viagemRepository.save(v);
        return ViagemRotaResponseDTO.fromEntity(v);
    }

    @Override
    public ViagemRotaResponseDTO buscar(UUID id) {
        ViagemRota v = viagemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viagem não encontrada"));
        return ViagemRotaResponseDTO.fromEntity(v);
    }

    @Override
    public List<ViagemRotaResponseDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("fim não pode ser antes de inicio");
        }
        return viagemRepository.findBySaidaPrevistaBetween(inicio, fim).stream()
                .map(ViagemRotaResponseDTO::fromEntity)
                .toList();
    }
}
