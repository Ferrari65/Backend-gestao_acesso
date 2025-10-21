package com.controller.docs;

import com.domain.user.Enum.TipoPessoa;
import com.dto.registroAcesso.AcessoCreatePorMatriculaRequest;
import com.dto.registroAcesso.AcessoResponse;
import com.dto.registroAcesso.AcessoSaidaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AcessoControllerDocs {
    @Operation(
            summary = "Registrar ENTRADA (por Matrícula/Documento)",
            description = "Cria um registro de acesso informando a **matriculaOuDocumento** do condutor/responsável e, opcionalmente, " +
                    "uma lista de **ocupanteMatriculas** (somente para COLABORADOR).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AcessoCreatePorMatriculaRequest.class),
                            examples = @ExampleObject(
                                    name = "Entrada por matrícula (com ocupante)",
                                    value = """
                                            {
                                              "tipoPessoa": "COLABORADOR",
                                              "matriculaOuDocumento": "0208",
                                              "codPortaria": 2,
                                              "observacao": "Chegada para o expediente. Acompanhado de estagiário.",
                                              "ocupanteMatriculas": ["0205"]
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponse(responseCode = "201", description = "Entrada registrada com sucesso.",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))

    ResponseEntity<AcessoResponse> criarPorMatricula(
            @RequestBody @Valid AcessoCreatePorMatriculaRequest req
    );

    @Operation(
            summary = "Registrar SAÍDA do acesso",
            description = "Fecha o registro de acesso (define data/hora de saída). Corpo da requisição é opcional para observação.",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                    description = "ID **(UUID)** do registro de acesso a ser fechado.")
    )
    @ApiResponse(responseCode = "200", description = "Saída registrada com sucesso.",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    ResponseEntity<AcessoResponse> registrarSaida(
            @PathVariable UUID id,
            @RequestBody(required = false) AcessoSaidaRequest req
    );

    @Operation(
            summary = "Listar acessos abertos",
            description = "Retorna todos os registros cujo campo `SAIDA` ainda é **null**.",
            parameters = @Parameter(
                    name = "abertos", in = ParameterIn.QUERY, required = true,
                    description = "Se `TRUE`, lista apenas acessos em aberto (**obrigatório**)."
            )
    )
    @ApiResponse(responseCode = "200", description = "Lista de acessos em aberto.",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    List<AcessoResponse> listarAbertos(@RequestParam boolean abertos);

    @Operation(
            summary = "Histórico de acessos (DATA)",
            description = """
                    Consulta por intervalo de **datas** (sem hora).
                    Se não informar, usa `DE = HOJE - 7 DIAS` e `ATE = HOJE`.
                    """,
            parameters = {
                    @Parameter(name = "de", in = ParameterIn.QUERY, required = false,
                            description = "Data inicial (yyyy-MM-dd)",
                            example = "2024-09-18"),
                    @Parameter(name = "ate", in = ParameterIn.QUERY, required = false,
                            description = "Data final (yyyy-MM-dd)",
                            example = "2024-09-25")
            }
    )
    @ApiResponse(responseCode = "200", description = "Lista de acessos no período filtrado.",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    List<AcessoResponse> historicoPorData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate
    );

    @Operation(
            summary = "Histórico por PORTARIA (apenas portaria)",
            description = "Filtra histórico apenas por `cod` da portaria (sem filtro de data)."
    )
    @ApiResponse(responseCode = "200", description = "Lista filtrada por portaria.",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    List<AcessoResponse> historicoPorPortaria(@RequestParam Short cod);

    @Operation(
            summary = "Histórico por TIPO DE PESSOA (apenas tipo)",
            description = "Filtra histórico apenas por `tipo` (COLABORADOR ou VISITANTE), sem filtro de data."
    )
    @ApiResponse(responseCode = "200", description = "Lista filtrada por tipo de pessoa.",
            content = @Content(schema = @Schema(implementation = AcessoResponse.class)))
    List<AcessoResponse> historicoPorPessoa(@RequestParam TipoPessoa tipo);
}
