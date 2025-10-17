package com.controller;

import com.dto.localizacao.Viagem.ViagemRotaRequestDTO;
import com.dto.localizacao.Viagem.ViagemRotaResponseDTO;
import com.services.ViagemRotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viagens")
@RequiredArgsConstructor
@Tag(name = "Viagens", description = "Endpoints para gerenciamento de viagens ")
public class ViagemRotaController {

    private final ViagemRotaService service;

    @PostMapping
    @Operation(summary = "Criar viagem")
    @ApiResponse(responseCode = "201", description = "Criado")
    public ResponseEntity<ViagemRotaResponseDTO> criar(@RequestBody @Valid ViagemRotaRequestDTO dto) {
        var resp = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar viagem por ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Viagem não encontrada", content = @Content)
    public ViagemRotaResponseDTO buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }

    @GetMapping
    @Operation(
            summary = "Listar viagens",
            description = "Lista geral sem filtros de data. Filtros opcionais: ativo e idRota."
    )
    public List<ViagemRotaResponseDTO> listar(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Integer idRota
    ) {
        return service.listar(ativo, idRota);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar viagem (substituição parcial)")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(responseCode = "404", description = "Viagem não encontrada", content = @Content)
    public ViagemRotaResponseDTO atualizar(@PathVariable UUID id,
                                           @RequestBody @Valid ViagemRotaRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar viagem (soft-delete)")
    @ApiResponse(responseCode = "204", description = "Inativado")
    @ApiResponse(responseCode = "404", description = "Viagem não encontrada", content = @Content)
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/viagens/{id}/ativo")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Ativar/Inativar viagem (toggle de ativo)")
    public ResponseEntity<Void> atualizarAtivo(
            @PathVariable UUID id,
            @RequestParam boolean value
    ) {
        service.atualizarAtivo(id, value);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/viagens")
    public ResponseEntity<List<ViagemRotaResponseDTO>> listarPorDia(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            LocalDate data
    ) {
        var lista = service.listarPorData(data);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }
}
