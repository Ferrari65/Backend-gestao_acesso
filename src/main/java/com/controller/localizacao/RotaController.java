package com.controller.localizacao;

import com.dto.PATCH.RotaPatchDTO;
import com.dto.localizacao.Rota.RotaDTO;
import com.dto.localizacao.Rota.RotaListDTO;
import com.dto.localizacao.Rota.RotaPontoItemDTO;
import com.dto.localizacao.Rota.RotaRequestDTO;
import com.services.impl.RotaServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/rotas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Rota", description = "Endpoints para gerenciamento de Rota")
public class RotaController implements com.controller.docs.RotaControllerDocs {

    private final RotaServiceImpl service;

    @GetMapping
    @Override
    public ResponseEntity<List<RotaListDTO>> listar() {
        var list = service.listar().stream().map(RotaListDTO::from).toList();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

//    @GetMapping("/{id}")
//    @Override
//    public ResponseEntity<RotaDTO> buscar(@Parameter(description = "ID da rota a ser buscada.", example = "1") @PathVariable Integer id) {
//        return ResponseEntity.ok(RotaDTO.from(service.buscar(id)));
//    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<RotaDTO> criar(@Valid @RequestBody RotaRequestDTO dto,
                                         UriComponentsBuilder uri) {
        var salvo = service.criar(dto);
        return ResponseEntity.created(
                uri.path("/rotas/{id}").buildAndExpand(salvo.getIdRota()).toUri()
        ).body(RotaDTO.from(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<RotaDTO> atualizar(@Parameter(description = "ID da rota a ser atualizada.", example = "1") @PathVariable Integer id,
                                             @Valid @RequestBody RotaRequestDTO dto) {
        var atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(RotaDTO.from(atualizado));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<RotaDTO> patch(@Parameter(description = "ID da rota a ser atualizada parcialmente.", example = "1") @PathVariable Integer id,
                                         @RequestBody RotaPatchDTO dto) {
        var atualizado = service.patch(id, dto);
        return ResponseEntity.ok(RotaDTO.from(atualizado));
    }

    @DeleteMapping("/{idRota}")
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<Void> deletar(@Parameter(description = "ID da rota a ser deletada.", example = "1") @PathVariable Integer idRota) {
        service.deletar(idRota);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idRota}/trajeto")
    @PreAuthorize("hasAnyRole('COLABORADOR','LIDER','GESTOR')")
    @Override
    public ResponseEntity<List<RotaPontoItemDTO>> trajeto(@PathVariable Integer idRota) {
        var dto = service.listarTrajeto(idRota);
        return dto.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dto);
    }
}