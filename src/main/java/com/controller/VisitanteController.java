package com.controller;

import com.controller.docs.VisitanteControllerDocs;
import com.dto.PATCH.VisitanteAtivoPatchRequest;
import com.dto.visitante.VisitanteCreateRequest;
import com.dto.visitante.VisitanteResponse;
import com.services.visitante.VisitanteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/visitantes")
@RequiredArgsConstructor
@Tag(name = "Visitantes", description = "Endpoints para Gerenciamento de visitantes")
public class VisitanteController implements VisitanteControllerDocs {

    private final VisitanteService service;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<VisitanteResponse> criar(@RequestBody @Valid VisitanteCreateRequest req) {
        VisitanteResponse resp = service.criar(req);
        return ResponseEntity
                .created(URI.create("/visitantes/" + resp.id()))
                .body(resp);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<VisitanteResponse>> listar(
            @RequestParam(value = "ativos", required = false) Boolean somenteAtivos
    ) {
        List<VisitanteResponse> lista = service.listar(somenteAtivos);
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @Override
    @PatchMapping("/{id}/ativo")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<VisitanteResponse> patchAtivo(
            @PathVariable UUID id,
            @RequestBody @Valid VisitanteAtivoPatchRequest body
    ) {
        VisitanteResponse atualizado = service.patchAtivo(id, body);
        return ResponseEntity.ok(atualizado);
    }
}