package com.services;

import com.dto.localizacao.Viagem.ViagemRotaRequestDTO;
import com.dto.localizacao.Viagem.ViagemRotaResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ViagemRotaService {

    ViagemRotaResponseDTO criar(ViagemRotaRequestDTO dto);
    ViagemRotaResponseDTO buscar(UUID id);
    List<ViagemRotaResponseDTO> listar(Boolean ativo, Integer idRota);
    ViagemRotaResponseDTO atualizar(UUID id, ViagemRotaRequestDTO dto);
    void inativar(UUID id);
    List<ViagemRotaResponseDTO> listarPorData(LocalDate data);
    void atualizarAtivo(UUID id, boolean ativo);
}
