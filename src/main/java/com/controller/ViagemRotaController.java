package com.controller;

import com.dto.localizacao.Viagem.ViagemRotaRequestDTO;
import com.dto.localizacao.Viagem.ViagemRotaResponseDTO;
import com.services.ViagemRotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viagens")
@RequiredArgsConstructor
public class ViagemRotaController {
    private final ViagemRotaService service;

    @PostMapping
    public ViagemRotaResponseDTO criar(@RequestBody @Valid ViagemRotaRequestDTO dto){
        return service.criar(dto);
    }

    @GetMapping("/{id}")
    public  ViagemRotaResponseDTO buscar(@PathVariable UUID id){
        return service.buscar(id);
    }

    @GetMapping("/periodo")
    public List<ViagemRotaResponseDTO> porPeriodo (
            LocalDate inicio,
            LocalDate fim){
        return service.listarPorPeriodo(inicio,fim);
    }

}
