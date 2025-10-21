package com.controller;

import com.dto.colaborador.ColaboradorDTO;
import com.services.colaborador.ColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/colaboradores")
@Tag(name="Colaboradores", description = "Endpoints para gerenciamento de Colaborador")
public class ColaboradorController implements com.controller.docs.ColaboradorControllerDocs {
    private final ColaboradorService service;

    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    @GetMapping
    @Override
    public ResponseEntity<List<ColaboradorDTO>> listarColaborador() {
        var list = service.listar();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{idColaborador}")
    @Override
    public ResponseEntity<ColaboradorDTO> buscarPorId(@PathVariable UUID idColaborador) {
        return ResponseEntity.ok(service.buscarPorId(idColaborador));
    }
}