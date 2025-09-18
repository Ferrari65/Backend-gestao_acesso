package com.services.veiculo;

import com.dto.veiculo.VeiculoRequestDTO;
import com.dto.veiculo.VeiculoResponseDTO;

import java.util.List;

public interface VeiculoService {

    VeiculoResponseDTO criar(VeiculoRequestDTO dto);
    List<VeiculoResponseDTO> listarAtivos();
    VeiculoResponseDTO buscarPorId(Long id);
    VeiculoResponseDTO atualizar(Long id, VeiculoRequestDTO dto);
    void desativar(Long id);
    void reativar(Long id);
}
