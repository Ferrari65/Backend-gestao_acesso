package com.controller;

import com.dto.colaborador.RotaColaboradorResponse;
import com.services.colaborador.RotaColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rotaColaborador")
@Tag(name = "Rotas - Colaboradores", description = "Endpoints para Gerenciamento e atribuição de Rota Colaborador")
@SecurityRequirement(name = "bearerAuth")
public class RotaColaboradorController {

    private final RotaColaboradorService service;

    @PutMapping("/{idRota}/{idColaborador}")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Atribuir colaborador à rota")
    public ResponseEntity<RotaColaboradorResponse> atribuir(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador,
            @RequestParam(required = false) Integer idPonto
    ) {
        var resp = service.atribuir(idRota, idColaborador, idPonto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping ("/{idRota}/colaboradores" )
    @PreAuthorize("hasAnyRole('GESTOR','LIDER','COLABORADOR')")
    @Operation(summary = "Listar colaboradores atribuídos a uma rota")
    public ResponseEntity<List<RotaColaboradorResponse>> listarPorRota(@PathVariable Integer idRota) {
        var resp = service.listarPorRota(idRota);
        return resp.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{idRota}/{idColaborador}")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Remover colaborador da rota")
    public ResponseEntity<Void> remover(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    ) {
        service.remover(idRota, idColaborador);
        return ResponseEntity.noContent().build();
    }

    @GetMapping ("/{idColaborador}/rota")
    @PreAuthorize("hasAnyRole('GESTOR','LIDER','COLABORADOR')")
    @Operation(summary = "Listar rota do colaborador")
    public ResponseEntity<List<RotaColaboradorResponse>> listar(@PathVariable UUID idColaborador) {
        var lista = service.listarPorColaborador(idColaborador);
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }
}
