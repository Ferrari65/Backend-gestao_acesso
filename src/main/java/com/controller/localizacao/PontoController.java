package com.controller.localizacao;

import com.controller.docs.PontoControllerDocs;
import com.domain.user.endereco.Pontos;
import com.dto.localizacao.Ponto.PontoDTO;
import com.dto.localizacao.Ponto.PontosRequestDTO;
import com.services.localizacao.impl.PontoServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('GESTOR')")
@RequestMapping("/pontos")
@Tag(name = "Pontos", description = "Endpoints para manipulação completa de recursos de Pontos (CRUD).")
public class PontoController implements PontoControllerDocs {

    private final PontoServiceImpl service;

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    @Override
    public ResponseEntity<List<PontoDTO>> listar() {
        var lista = service.listar().stream().map(PontoDTO::from).toList();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('GESTOR','LIDER')")
    @Override
    public ResponseEntity<PontoDTO> buscar(@PathVariable Integer id) {
        Pontos p = service.buscar(id);
        return ResponseEntity.ok(PontoDTO.from(p));
    }


    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<PontoDTO> atualizar(@PathVariable Integer id,
                                              @Valid @RequestBody PontosRequestDTO dto) {
        Pontos atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(PontoDTO.from(atualizado));
    }

    @PostMapping
    @Override
    public ResponseEntity<PontoDTO> criar(@Valid @RequestBody PontosRequestDTO dto) {
        Pontos salvo = service.criar(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getIdPonto())
                .toUri();
        return ResponseEntity.created(location).body(PontoDTO.from(salvo));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    @Override
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
