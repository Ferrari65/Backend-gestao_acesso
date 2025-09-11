package com.controller.localizacao;

import com.domain.user.Rotas.Rota;
import com.dto.localizacao.Rota.RotaDTO;
import com.dto.localizacao.Rota.RotaRequestDTO;
import com.services.localizacao.RotaService;
import io.swagger.v3.oas.annotations.Operation;
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
@SecurityRequirement(name="bearerAuth")
@Tag(name = "Rota", description = "Endpoints para manipulação completa de recursos de Rota (CRUD)")
public class RotaController {

    private final RotaService service;

    @GetMapping
    @Operation (summary = "Listar todas as Rotas cadastradas",
            description = "Retorna uma lista de todas as rotas cadastradas. A lista pode estar vazia se não houverem rotas no sistema.")
    public ResponseEntity<List<RotaDTO>> listar(){
        var list = service.listar().stream().map(RotaDTO::from).toList();
        return list.isEmpty()? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @Operation (summary = "Buscar Rota por ID",
            description = "Retorna os dados de uma Rota específica utilizando seu ID. O ID da Rota deve ser passado como um parâmetro de caminho na URL. Se o ID não for encontrado, a API retornará um erro 404 (Not Found).")
    @GetMapping("/{id}") public ResponseEntity<RotaDTO> buscar(@PathVariable Integer id){
        return ResponseEntity.ok(RotaDTO.from(service.buscar(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    @Operation (summary = "Criar uma nova Rota",
            description = "Cria uma nova rota com os dados fornecidos no corpo da requisição. O ID da rota é gerado automaticamente pelo sistema. Em caso de sucesso, retorna o objeto RotaDTO criado e o status 201 (Created), com a URL do novo recurso.")
    public ResponseEntity<RotaDTO> criar (@Valid @RequestBody RotaRequestDTO dto,
                                          UriComponentsBuilder uri){
        var salvo = service.criar(dto);
        return ResponseEntity.created(uri.path("/rotas/{id}").buildAndExpand(salvo.getIdRota()).toUri())
                .body(RotaDTO.from(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(
            summary = "Atualizar uma Rota existente",
            description = "Atualiza nome, período, capacidade, ativo, horários e a sequência de pontos (a lista é substituída por completo). Retorna 404 se a rota não existir e 409 se houver conflito de nome na cidade."
    )
    public ResponseEntity<RotaDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody RotaRequestDTO dto
    ) {
        var atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(RotaDTO.from(atualizado));
    }

}
