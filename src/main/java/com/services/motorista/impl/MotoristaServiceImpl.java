package com.services.motorista.impl;

import com.dto.Motorista.MotoristaResponseDTO;
import com.repositories.Motorista.MotoristaRepository;
import com.services.motorista.MotoristaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MotoristaServiceImpl implements MotoristaService {


    private final MotoristaRepository repository;

    @Override
    public List<MotoristaResponseDTO> listarAtivos(){
        return repository.findByAtivoTrue()
                .stream()
                .map(m -> new MotoristaResponseDTO(
                        m.getId(),
                        m.getNome(),
                        m.getCnh(),
                        m.getTelefone(),
                        m.getEmpresaTerceiro(),
                        m.getDataVencCnh()
                ))
                .toList();
    }

}
