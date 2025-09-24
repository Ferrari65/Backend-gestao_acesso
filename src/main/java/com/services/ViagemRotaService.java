package com.services;

import com.dto.localizacao.Viagem.ViagemRotaRequestDTO;
import com.dto.localizacao.Viagem.ViagemRotaResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ViagemRotaService {

    ViagemRotaResponseDTO criar (ViagemRotaRequestDTO dto);
    ViagemRotaResponseDTO buscar (UUID id);
    List<ViagemRotaResponseDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim);
}

