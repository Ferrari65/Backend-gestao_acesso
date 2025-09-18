package com.services.motorista;

import com.dto.Motorista.MotoristaResponseDTO;

import java.util.List;

public interface MotoristaService {
    List<MotoristaResponseDTO> listarAtivos();
}
