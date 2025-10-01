package com.controller;

import com.dto.liderRota.LiderRotaResponse;
import com.services.impl.LiderRotaServiceImpl;
import com.services.liderRota.LiderRotaService;
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
@RequestMapping("/rotas/{idRota}/lideres")
@Tag(name = "Líderes de Rotas",
        description = "Endpoints para gerenciamento de líderes (atribuir, listar e remover).")
@SecurityRequirement(name = "bearerAuth")
public class LiderRotaController implements com.controller.docs.LiderRotaControllerDocs {

    private final LiderRotaService service;

    @PutMapping("/{idColaborador}")
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<LiderRotaResponse> atribuir(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    ) {
        var resp = service.atribuir(idRota, idColaborador);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR','LIDER','COLABORADOR')")
    @Override
    public ResponseEntity<List<LiderRotaResponse>> listar(@PathVariable Integer idRota) {
        var resp = service.buscarTodos(idRota);
        if (resp.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{idColaborador}")
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<Void> remover(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    ) {
        service.remover(idRota, idColaborador);
        return ResponseEntity.noContent().build();
    }
}