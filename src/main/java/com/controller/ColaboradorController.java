package com.controller;

import com.dto.colaborador.ColaboradorDTO;
import com.repositories.UserRepository;
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
public class ColaboradorController {
    private final ColaboradorService service;

    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    @GetMapping
    @Operation(summary = "Listar todos colaboradores",
            description = "Listar todos colaboradores",
        responses = {
            @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ColaboradorDTO.class))
                            )
                    }),
                @ApiResponse (description = "No Content", responseCode = "204" , content = @Content),
                @ApiResponse (description = "Bad Request", responseCode = "400", content = @Content),
                @ApiResponse (description = "Unauthorized", responseCode = "401", content = @Content),
                @ApiResponse (description = "Not Found", responseCode = "404", content = @Content),
                @ApiResponse (description = "Internal Server Error", responseCode = "500",content = @Content)
        }
    )
    public ResponseEntity<List<ColaboradorDTO>> listarColaborador() {
        var list = service.listar();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{idColaborador}")
    public ResponseEntity<ColaboradorDTO> buscarPorId(@PathVariable UUID idColaborador) {
        return ResponseEntity.ok(service.buscarPorId(idColaborador));
    }
}