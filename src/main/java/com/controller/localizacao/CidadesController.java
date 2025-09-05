package com.controller.localizacao;

import com.domain.user.endereco.Cidade;
import com.dto.localizacao.CidadeRequestDTO;
import com.repositories.localizacao.CidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@PreAuthorize("hasRole('GESTOR')")
@RequestMapping("/cidades")
@RequiredArgsConstructor
@Tag(name = "Cidades", description = "Endpoints de Cidade")
public class CidadesController {

    private final CidadeRepository cidadeRepository;

    @GetMapping(path = {"/listarCidades"})
    @Operation(summary = "Listar todas as Cidades")
    public ResponseEntity<List<Cidade>> listarTodas() {
        var lista = cidadeRepository.findAll();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @PostMapping("/cadastrarCidades")
    public ResponseEntity<Cidade> criarCidade(@Valid @RequestBody CidadeRequestDTO dto,
                                              UriComponentsBuilder uriBuilder) {
        String nome = dto.nome().trim();
        String uf = dto.uf().toUpperCase();

        if (cidadeRepository.existsByNomeIgnoreCaseAndUfIgnoreCase(nome, uf)) {
            return ResponseEntity.status(409).build();
        }

        var c = new Cidade();
        c.setNome(nome);
        c.setUf(uf);
        c = cidadeRepository.save(c);

        var uri = uriBuilder.path("/cidades/{id}").buildAndExpand(c.getIdCidade()).toUri();
        return ResponseEntity.created(uri).body(c);
    }
}
