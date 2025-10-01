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
@RequestMapping("/rotas/{idRota}/colaboradores")
@Tag(name = "Rotas - Colaboradores")
@SecurityRequirement(name = "bearerAuth")
public class RotaColaboradorController {

    private final RotaColaboradorService service;

    @PutMapping("/{idColaborador}")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Atribuir colaborador à rota (idempotente). Pode definir/atualizar idPonto.")
    public ResponseEntity<RotaColaboradorResponse> atribuir(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador,
            @RequestParam(required = false) Integer idPonto
    ) {
        var resp = service.atribuir(idRota, idColaborador, idPonto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR','LIDER','COLABORADOR')")
    @Operation(summary = "Listar colaboradores atribuídos a uma rota")
    public ResponseEntity<List<RotaColaboradorResponse>> listarPorRota(@PathVariable Integer idRota) {
        var resp = service.listarPorRota(idRota);
        return resp.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{idColaborador}")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Remover colaborador da rota (idempotente)")
    public ResponseEntity<Void> remover(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    ) {
        service.remover(idRota, idColaborador);
        return ResponseEntity.noContent().build();
    }
}