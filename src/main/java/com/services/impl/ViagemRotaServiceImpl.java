package com.services.impl;

import com.domain.user.ViagemRota;
import com.dto.localizacao.Viagem.ViagemRotaRequestDTO;
import com.dto.localizacao.Viagem.ViagemRotaResponseDTO;
import com.repositories.viagem.ViagemRepository;
import com.services.ViagemRotaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViagemRotaServiceImpl implements ViagemRotaService {

    private final ViagemRepository viagemRepository;

    @Override
    @Transactional
    public ViagemRotaResponseDTO criar(ViagemRotaRequestDTO dto) {

        if (dto.saidaPrevista() != null && dto.chegadaPrevista() != null
                && dto.chegadaPrevista().isBefore(dto.saidaPrevista())) {
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
    @Transactional(readOnly = true)
    public ViagemRotaResponseDTO buscar(UUID id) {
        return ViagemRotaResponseDTO.fromEntity(pegar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViagemRotaResponseDTO> listar(Boolean ativo, Integer idRota) {

        if (ativo != null && idRota != null) {
            return viagemRepository.findByIdRota(idRota).stream()
                    .filter(v -> v.isAtivo() == ativo)
                    .map(ViagemRotaResponseDTO::fromEntity)
                    .toList();
        }
        if (ativo != null) {
            return viagemRepository.findByAtivo(ativo).stream()
                    .map(ViagemRotaResponseDTO::fromEntity)
                    .toList();
        }
        if (idRota != null) {
            return viagemRepository.findByIdRota(idRota).stream()
                    .map(ViagemRotaResponseDTO::fromEntity)
                    .toList();
        }
        return viagemRepository.findAll().stream()
                .map(ViagemRotaResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public ViagemRotaResponseDTO atualizar(UUID id, ViagemRotaRequestDTO dto) {
        var v = pegar(id);

        if (dto.idRota() != null)          v.setIdRota(dto.idRota());
        if (dto.idMotorista() != null)     v.setIdMotorista(dto.idMotorista());
        if (dto.idVeiculo() != null)       v.setIdVeiculo(dto.idVeiculo());

        if (dto.saidaPrevista() != null)   v.setSaidaPrevista(dto.saidaPrevista());
        if (dto.chegadaPrevista() != null) v.setChegadaPrevista(dto.chegadaPrevista());
        if (v.getSaidaPrevista() != null && v.getChegadaPrevista() != null
                && v.getChegadaPrevista().isBefore(v.getSaidaPrevista())) {
            throw new IllegalArgumentException("chegadaPrevista não pode ser antes de saidaPrevista");
        }

        if (dto.tipoViagem() != null)      v.setTipoViagem(dto.tipoViagem());

        return ViagemRotaResponseDTO.fromEntity(viagemRepository.save(v));
    }

    @Override
    @Transactional
    public void inativar(UUID id) {
        var v = pegar(id);
        v.setAtivo(false);
        viagemRepository.save(v);
    }

    @Override
    @Transactional
    public void reativar(UUID id) {
        var v = pegar(id);
        v.setAtivo(true);
        viagemRepository.save(v);
    }

    private ViagemRota pegar(UUID id) {
        return viagemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viagem não encontrada"));
    }
}
