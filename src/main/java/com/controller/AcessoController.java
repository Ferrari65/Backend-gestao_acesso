package com.controller;

import com.dto.registroAcesso.AcessoCreatePorMatriculaRequest;
import com.dto.registroAcesso.AcessoCreateRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/acessos")
@RequiredArgsConstructor
@Tag(name = "Acessos (Portaria)", description = "Registro de entrada/saída de colaboradores/visitantes na portaria")
@SecurityRequirement(name = "bearerAuth")
public class AcessoController {

    private final AcessoService service;

    @Operation(
            summary = "Registrar ENTRADA (POR ID)",
            description = "Cria um registro de acesso com base no **idPessoa** (UUID do condutor) e no **codPortaria**. "
                    + "Ocupantes (se houver) são sempre colaboradores (UUIDs).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AcessoCreateRequest.class),
                            examples = @ExampleObject(name = "Entrada por UUID",
                                    value = """
                            {
                              "tipoPessoa": "COLABORADOR",
                              "idPessoa": "8e9e9a6f-1a2b-4d0e-8c1a-5e5f11b8b9aa",
                              "codPortaria": (1 ,2, 3, 4 ou 5),
                              "observacao": "Inicio do expediente",
                              "ocupantes": [
                                "coloque o id dos acompanhantes"
                              ]
                            }
                            """)))
    )
    @ApiResponse(responseCode = "201", description = "Entrada registrada",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    @ApiResponse(responseCode = "409", description = "Já existe acesso aberto para o condutor", content = @Content)
    @PostMapping
    public ResponseEntity<AcessoResponse> criar(
            @org.springframework.web.bind.annotation.RequestBody @Valid AcessoCreateRequest req) {
        var resp = service.criar(req);
        return ResponseEntity.created(URI.create("/acessos/" + resp.id())).body(resp);
    }

    @Operation(
            summary = "Registrar ENTRADA (por matrícula/documento)",
            description = "Atalho para criar acesso informando **matriculaOuDocumento** do condutor e, opcionalmente, "
                    + "uma lista de **ocupantesMatriculas**.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AcessoCreatePorMatriculaRequest.class),
                            examples = @ExampleObject(name = "Entrada por matrícula",
                                    value = """
                            {
                              "tipoPessoa": "COLABORADOR",
                              "matriculaOuDocumento": "A12345",
                              "codPortaria": (1,2,3,4 ou 5) ,
                              "observacao": "chegada ao expediente",
                              "ocupanteMatriculas": ["digite a matricula do ocupante"]
                            }
                            """)))
    )
    @ApiResponse(responseCode = "201", description = "Entrada registrada",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    @ApiResponse(responseCode = "404", description = "Matrícula/Documento não encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Já existe acesso aberto para o condutor", content = @Content)
    @PostMapping("/por-matricula")
    public ResponseEntity<AcessoResponse> criarPorMatricula(
            @org.springframework.web.bind.annotation.RequestBody @Valid AcessoCreatePorMatriculaRequest req) {
        var resp = service.criarPorMatricula(req);
        return ResponseEntity.created(URI.create("/acessos/" + resp.id())).body(resp);
    }

    @Operation(
            summary = "Registrar SAÍDA do acesso",
            description = "Fecha o registro de acesso (define data/hora de saída). O corpo é opcional para observação.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                            description = "ID do registro de acesso (UUID)",
                            example = "67997a0d-9d94-4dbe-bd4e-a6fb66a679b3")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false,
                    content = @Content(schema = @Schema(implementation = AcessoSaidaRequest.class),
                            examples = @ExampleObject(name = "Observação de saída",
                                    value = """
                            { "observacao": "Saída antecipada" }
                            """)))
    )
    @ApiResponse(responseCode = "200", description = "Saída registrada",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Registro não encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Registro já estava fechado", content = @Content)
    @PostMapping("/{id}/saida")
    public ResponseEntity<AcessoResponse> registrarSaida(
            @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody(required = false) AcessoSaidaRequest req) {
        return ResponseEntity.ok(service.registrarSaida(id, req));
    }

    @Operation(
            summary = "Listar acessos abertos",
            description = "Retorna somente os registros com **saida = null** quando `abertos=true`.",
            parameters = {
                    @Parameter(name = "abertos", in = ParameterIn.QUERY, required = true,
                            description = "Se true, lista apenas acessos em aberto", example = "true")
            }
    )
    @ApiResponse(responseCode = "200", description = "Lista de acessos em aberto",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    @GetMapping(params = "abertos")
    public List<AcessoResponse> listarAbertos(@RequestParam boolean abertos) {
        return abertos ? service.listarAbertos() : List.of();
    }

    // GET /acessos/historico
    @Operation(
            summary = "Histórico de acessos",
            description = "Consulta paginável/filtrável por intervalo de datas (ISO-8601) e código de portaria.",
            parameters = {
                    @Parameter(name = "de", in = ParameterIn.QUERY, required = false,
                            description = "Data/hora inicial (ISO-8601). Default: agora - 7 dias",
                            example = "2025-10-10T00:00:00-03:00"),
                    @Parameter(name = "ate", in = ParameterIn.QUERY, required = false,
                            description = "Data/hora final (ISO-8601). Default: agora",
                            example = "2025-10-17T23:59:59-03:00"),
                    @Parameter(name = "codPortaria", in = ParameterIn.QUERY, required = false,
                            description = "Código interno da portaria (ex.: 23)", example = "23")
            }
    )
    @ApiResponse(responseCode = "200", description = "Lista de acessos no período",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    @GetMapping("/historico")
    public List<AcessoResponse> historico(
            @RequestParam(required = false) String de,
            @RequestParam(required = false) String ate,
            @RequestParam(required = false) Integer codPortaria) {
        OffsetDateTime d1 = de != null ? OffsetDateTime.parse(de) : OffsetDateTime.now().minusDays(7);
        OffsetDateTime d2 = ate != null ? OffsetDateTime.parse(ate) : OffsetDateTime.now();
        return service.listarHistorico(d1, d2, codPortaria);
    }
}
