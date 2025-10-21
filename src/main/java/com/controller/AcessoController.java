package com.controller;

import com.controller.docs.AcessoControllerDocs;
import com.domain.user.Enum.TipoPessoa;
import com.dto.registroAcesso.AcessoCreatePorMatriculaRequest;
import com.dto.registroAcesso.AcessoResponse;
import com.dto.registroAcesso.AcessoSaidaRequest;
import com.services.registroAcesso.AcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/acessos")
@RequiredArgsConstructor
@Tag(name = "Acessos (Portaria)", description = "Registro de entrada/saída de colaboradores e visitantes na portaria.")
@SecurityRequirement(name = "bearerAuth")
public class AcessoController implements AcessoControllerDocs {

    private final AcessoService service;

    @PostMapping("/por-matricula")
    @Override
    public ResponseEntity<AcessoResponse> criarPorMatricula(
            @RequestBody @Valid AcessoCreatePorMatriculaRequest req
    ) {
        var resp = service.criarPorMatricula(req);
        return ResponseEntity.created(URI.create("/acessos/" + resp.id())).body(resp);
    }

    @PostMapping("/{id}/saida")
    @Override
    public ResponseEntity<AcessoResponse> registrarSaida(
            @PathVariable UUID id,
            @RequestBody(required = false) AcessoSaidaRequest req
    ) {
        return ResponseEntity.ok(service.registrarSaida(id, req));
    }

    @GetMapping(params = "abertos")
    @Override
    public List<AcessoResponse> listarAbertos(@RequestParam boolean abertos) {
        return abertos ? service.listarAbertos() : List.of();
    }

    @GetMapping("/historico")
    @Override
    public List<AcessoResponse> historicoPorData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate
    ) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = (de != null) ? de : hoje.minusDays(7);
        LocalDate fim = (ate != null) ? ate : hoje;
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Parâmetro 'ate' não pode ser anterior a 'de'.");
        }
        return service.listarHistoricoPorData(inicio, fim);
    }

    @GetMapping("/historico-por-portaria")
    @Override
    public List<AcessoResponse> historicoPorPortaria(@RequestParam Short cod) {
        return service.listarHistoricoSomentePortaria(cod);
    }

    @GetMapping("/historico-por-pessoa")
    @Override
    public List<AcessoResponse> historicoPorPessoa(@RequestParam TipoPessoa tipo) {
        return service.listarHistoricoSomenteTipo(tipo);
    }
}
