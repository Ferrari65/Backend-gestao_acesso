package com.controller.localizacao;

import com.domain.user.endereco.Cidade;
import com.dto.localizacao.CidadeRequestDTO;
import com.repositories.localizacao.CidadeRepository;
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
public class CidadesController implements com.controller.localizacao.docs.CidadesControllerDocs {

    private final CidadeRepository cidadeRepository;

    @GetMapping(path = {"/listarCidades"})
    @Override
    public ResponseEntity<List<Cidade>> listarTodas() {
        var lista = cidadeRepository.findAll();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @PostMapping("/cadastrarCidade")
    @Override
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

    @PutMapping("/atualizarCidade/{id}")
    @Override
    public ResponseEntity<?> atualizarCidade(@PathVariable Integer id,
                                             @Valid @RequestBody CidadeRequestDTO dto){

        return cidadeRepository.findById(id)
                .map(c -> {
                    String nome = dto.nome().trim();
                    String uf   = dto.uf().toUpperCase();

                    if (cidadeRepository.existsByNomeIgnoreCaseAndUfIgnoreCaseAndIdCidadeNot(nome,uf,id)){
                        return ResponseEntity.status(409).build();
                    }

                    c.setNome(nome);
                    c.setUf(uf);
                    return ResponseEntity.ok(cidadeRepository.save(c));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deletarCidade/{id}")
    @Override
    public ResponseEntity<Void> deletarcidade(@PathVariable Integer id){
        if (!cidadeRepository.existsById(id)) return ResponseEntity.notFound().build();
        cidadeRepository.deleteById(id);
        return ResponseEntity.noContent().build();

    }
}
