package com.controller.docs;

import com.dto.PATCH.VisitanteAtivoPatchRequest;
import com.dto.visitante.VisitanteCreateRequest;
import com.dto.visitante.VisitanteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface VisitanteControllerDocs {

    @Operation(
            operationId = "criarVisitante",
            summary = "Cadastrar visitante",
            description = """
            Cria um novo visitante. A combinação (tipoDocumento, numeroDocumento) é única.
            Campos obrigatórios: nomeCompleto, tipoDocumento, numeroDocumento, pessoaAnfitria.
            """,
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VisitanteCreateRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "CPF_completo",
                                            value = """
                                            {
                                              "nomeCompleto": "Marcos Silva",
                                              "tipoDocumento": "CPF",
                                              "numeroDocumento": "12345678901",
                                              "dataNascimento": "1992-05-10",
                                              "telefone": "(16) 98888-7777",
                                              "empresaVisitante": "Fornecedor X",
                                              "pessoaAnfitria": "692e5d7c-02c8-4805-bb6b-3250572ab020",
                                              "motivoVisita": "Ajuste de equipamento",
                                              "ativo": true
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "RG_simples",
                                            value = """
                                            {
                                              "nomeCompleto": "Ana Beatriz Oliveira",
                                              "tipoDocumento": "RG",
                                              "numeroDocumento": "45299387",
                                              "pessoaAnfitria": "692e5d7c-02c8-4805-bb6b-3250572ab020"
                                            }
                                            """
                                    )
                            }
                    )
            )
    )
    @ApiResponse(
            description = "Visitante criado com sucesso.",
            responseCode = "201",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = VisitanteResponse.class)
            )
    )
    @ApiResponse(
            description = "Documento já cadastrado.",
            responseCode = "409",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            description = "Requisição inválida (validação).",
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            description = "Não autenticado.",
            responseCode = "401",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            description = "Não autorizado.",
            responseCode = "403",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<VisitanteResponse> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = VisitanteCreateRequest.class))
            )
            VisitanteCreateRequest req
    );

    @Operation(
            operationId = "listarVisitantes",
            summary = "Listar visitantes",
            description = """
            Retorna a lista de visitantes, ordenada por nome.
            Use o filtro opcional `ativos=true|false` para retornar somente ativos ou inativos.
            """,
            parameters = {
                    @Parameter(
                            name = "ativos",
                            in = ParameterIn.QUERY,
                            required = false,
                            description = "Filtra por status de ativo. Se omitido, retorna todos.",
                            example = "true"
                    )
            }
    )
    @ApiResponse(
            description = "Lista retornada com sucesso.",
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = VisitanteResponse.class))
            )
    )
    @ApiResponse(description = "Sem conteúdo.", responseCode = "204")
    @ApiResponse(
            description = "Não autenticado.",
            responseCode = "401",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            description = "Não autorizado.",
            responseCode = "403",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<List<VisitanteResponse>> listar(Boolean somenteAtivos);

    @Operation(
            operationId = "alterarStatusAtivoVisitante",
            summary = "Ativar/Inativar visitante",
            description = "Atualiza o campo `ativo` do visitante via PATCH.",
            parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            required = true,
                            description = "UUID do visitante.",
                            example = "408a7e45-63a7-499c-967e-00b682edb2a2"
                    )
            },
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VisitanteAtivoPatchRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "inativar",
                                            value = """
                                            { "ativo": false }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "ativar",
                                            value = """
                                            { "ativo": true }
                                            """
                                    )
                            }
                    )
            )
    )
    @ApiResponse(
            description = "Status atualizado com sucesso.",
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VisitanteResponse.class))
    )
    @ApiResponse(
            description = "Visitante não encontrado.",
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            description = "Não autenticado.",
            responseCode = "401",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            description = "Não autorizado.",
            responseCode = "403",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<VisitanteResponse> patchAtivo(
            @PathVariable UUID id,
            VisitanteAtivoPatchRequest body
    );
}