package com.controller;

import com.dto.impedimentos.ImpedimentoCreateRequest;
import com.dto.impedimentos.ImpedimentoResponse;
import com.services.impedimento.ImpedimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/impedimentos")
@RequiredArgsConstructor
@Tag(name = "Impedimentos", description = "Cadastro e gestão de impedimentos de viagens")
public class ImpedimentoController {

    private final ImpedimentoService service;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(
            summary = "Registrar um novo impedimento",
            description = """
                Cria um novo impedimento vinculado a uma viagem.
                Os campos <b>motivo</b> e <b>severidade</b> são enums e aparecem como dropdown no Swagger UI.
                """
    )
    public ResponseEntity<ImpedimentoResponse> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação de um impedimento"
            )
            @RequestBody @Valid ImpedimentoCreateRequest req
    ) {
        var resp = service.criar(req);
        return ResponseEntity
                .created(URI.create("/api/impedimentos/" + resp.id()))
                .body(resp);
    }

    @GetMapping
    @Operation(
            summary = "Listar impedimentos",
            description = "Lista todos os impedimentos ou apenas os ativos, se o parâmetro 'ativos' for true."
    )
    public List<ImpedimentoResponse> listar(
            @Parameter(
                    description = "Se true, retorna apenas impedimentos ativos. Se omitido, retorna todos."
            )
            @RequestParam(value = "ativos", required = false) Boolean apenasAtivos
    ) {
        return service.listar(apenasAtivos);
    }

    @PatchMapping("/{id}/inativar")
    @Operation(
            summary = "Inativar um impedimento",
            description = "Marca o impedimento como inativo e registra a data/hora da finalização."
    )
    public ImpedimentoResponse inativar(
            @Parameter(description = "ID do impedimento a ser inativado")
            @PathVariable UUID id
    ) {
        return service.inativar(id);
    }
}
