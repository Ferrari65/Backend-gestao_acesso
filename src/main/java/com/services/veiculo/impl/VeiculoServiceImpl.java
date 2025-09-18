package com.services.veiculo.impl;

import com.domain.user.veiculo.Veiculo;
import com.dto.veiculo.VeiculoRequestDTO;
import com.dto.veiculo.VeiculoResponseDTO;
import com.repositories.veiculo.VeiculoRepository;
import com.services.veiculo.VeiculoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository repository;

    @Override
    @Transactional
    public VeiculoResponseDTO criar(VeiculoRequestDTO dto) {
        if (dto.placa() != null && repository.existsByPlacaIgnoreCaseAndAtivoTrue(dto.placa())) {
            throw new IllegalArgumentException("Já existe veículo ativo com essa placa.");
        }

        Veiculo v = new Veiculo();
        aplicar(dto, v);
        v.setAtivo(true);

        Veiculo salvo = repository.save(v);
        return toResponse(salvo);
    }

    @Override
    public List<VeiculoResponseDTO> listarAtivos() {
        return repository.findByAtivoTrue()
                .stream().map(this::toResponse).toList();
    }

    @Override
    public VeiculoResponseDTO buscarPorId(Long id) {
        Veiculo v = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado ou inativo."));
        return toResponse(v);
    }

    @Override
    @Transactional
    public VeiculoResponseDTO atualizar(Long id, VeiculoRequestDTO dto) {
        Veiculo v = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado ou inativo."));

        if (dto.placa() != null && !dto.placa().equalsIgnoreCase(v.getPlaca())
                && repository.existsByPlacaIgnoreCaseAndAtivoTrue(dto.placa())) {
            throw new IllegalArgumentException("Já existe veículo ativo com essa placa.");
        }

        aplicar(dto, v);
        return toResponse(v);
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        Veiculo v = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado."));
        if (Boolean.FALSE.equals(v.getAtivo())) return;
        v.setAtivo(false);
    }

    @Override
    @Transactional
    public void reativar(Long id) {
        Veiculo v = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado."));
        if (Boolean.TRUE.equals(v.getAtivo())) return;
        if (v.getPlaca() != null && repository.existsByPlacaIgnoreCaseAndAtivoTrue(v.getPlaca())) {
            throw new IllegalArgumentException("Já existe veículo ativo com essa placa. Não é possível reativar.");
        }
        v.setAtivo(true);
    }


    private void aplicar(VeiculoRequestDTO dto, Veiculo v) {
        v.setTipoVeiculo(dto.tipoVeiculo());
        v.setPlaca(dto.placa());
        v.setCapacidade(dto.capacidade());
        v.setObservacao(dto.observacao());
    }

    private VeiculoResponseDTO toResponse(Veiculo v) {
        return new VeiculoResponseDTO(
                v.getId(),
                v.getPlaca(),
                v.getCapacidade(),
                v.getTipoVeiculo(),
                v.getObservacao(),
                v.getAtivo()
        );
    }
}
