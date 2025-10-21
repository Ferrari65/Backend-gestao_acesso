package com.controller;

import com.dto.registroEmbarque.RegistrarEmbarqueRequest;
import com.dto.registroEmbarque.RegistroEmbarqueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viagens/{idViagem}/embarques")
@RequiredArgsConstructor
@Tag(name = "Registro-Embarque",
        description = "Endpoints para gerenciamento de Registro de embarque")
@SecurityRequirement(name = "bearerAuth")

public class RegistroEmbarqueController implements com.controller.docs.RegistroEmbarqueControllerDocs {

    private final com.services.registroEmbarque.RegistroEmbarqueService service;

    @PostMapping
    @PreAuthorize("hasRole('LIDER')")
    @Override
    public ResponseEntity<RegistroEmbarqueResponse> registrar(
            @PathVariable UUID idViagem,
            @RequestBody RegistrarEmbarqueRequest request,
            @AuthenticationPrincipal(expression = "id") UUID idValidador
    ) {
        var resp = service.registrar(idViagem, request, idValidador);
        return ResponseEntity
                .created(URI.create("/viagens/" + idViagem + "/embarques/" + resp.getIdEmbarque()))
                .body(resp);

    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<List<RegistroEmbarqueResponse>> listarTodos(@PathVariable UUID idViagem) {
        return ResponseEntity.ok(service.listarTodos(idViagem));
    }

}