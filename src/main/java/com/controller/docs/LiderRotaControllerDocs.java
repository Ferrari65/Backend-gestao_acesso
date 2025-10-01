package com.controller.docs;

import com.dto.liderRota.LiderRotaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface LiderRotaControllerDocs {

    @Operation(
            operationId = "atribuirLiderARota",
            summary = "Atribuir/reativar liderança na rota",
            description = """
            Promove o colaborador (já atribuído à rota) a líder.
            Regra: o colaborador deve estar no PONTO de ORDEM = 1 na tabela rota_ponto.
            Se já for líder inativo, a liderança é reativada; se já for ativo, é idempotente.
            """,
            parameters = {
                    @Parameter(name = "idRota", in = ParameterIn.PATH, required = true,
                            description = "Identificador numérico da rota.", example = "42"),
                    @Parameter(name = "idColaborador", in = ParameterIn.PATH, required = true,
                            description = "UUID do colaborador a ser atribuído como líder.",
                            example = "55efc89a-e69c-4020-a853-e292885f736b")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Liderança criada/reativada com sucesso.",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LiderRotaResponse.class)
                    )
            ),
            @ApiResponse(
                    description = "Regra de negócio violada (ex.: colaborador não está no ponto de ORDEM 1; não está atribuído à rota; não possui ponto definido).",
                    responseCode = "422",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = {
                                    @ExampleObject(
                                            name = "LIDER_PONTO_NAO_E_ORDEM_1",
                                            value = """
                        {
                          "type": "about:blank",
                          "title": "Regra de negócio violada",
                          "status": 422,
                          "detail": "Apenas colaboradores no ponto de ORDEM 1 podem ser líderes dessa rota.",
                          "code": "LIDER_PONTO_NAO_E_ORDEM_1",
                          "path": "/rotas/42/lideres/55efc89a-e69c-4020-a853-e292885f736b",
                          "method": "PUT"
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "COLABORADOR_NAO_ATRIBUIDO",
                                            value = """
                        {
                          "type": "about:blank",
                          "title": "Regra de negócio violada",
                          "status": 422,
                          "detail": "Colaborador não está atribuído a esta rota.",
                          "code": "COLABORADOR_NAO_ATRIBUIDO",
                          "path": "/rotas/42/lideres/55efc89a-e69c-4020-a853-e292885f736b",
                          "method": "PUT"
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    description = "Rota ou colaborador não localizado.",
                    responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    description = "Não autenticado.",
                    responseCode = "401",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    description = "Não autorizado.",
                    responseCode = "403",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<LiderRotaResponse> atribuir(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    );

    @Operation(
            operationId = "listarLideresDaRota",
            summary = "Listar líderes ATIVOS da rota",
            description = "Retorna a relação de líderes atualmente ATIVOS vinculados à rota especificada.",
            parameters = {
                    @Parameter(name = "idRota", in = ParameterIn.PATH, required = true,
                            description = "Identificador numérico da rota.", example = "42")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Lista de líderes retornada com sucesso.",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = LiderRotaResponse.class))
                    )
            ),
            @ApiResponse(description = "A rota não possui líderes ativos.", responseCode = "204"),
            @ApiResponse(
                    description = "Rota não localizada.",
                    responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    description = "Não autenticado.",
                    responseCode = "401",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    description = "Não autorizado.",
                    responseCode = "403",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<List<LiderRotaResponse>> listar(@PathVariable Integer idRota);

    @Operation(
            operationId = "removerLiderDaRota",
            summary = "Inativar liderança do colaborador na rota",
            description = "Marca o vínculo do colaborador como INATIVO para a rota informada. Não remove histórico."
    )
    @ApiResponses({
            @ApiResponse(description = "Liderança inativada.", responseCode = "204"),
            @ApiResponse(
                    description = "Rota ou liderança não localizada.",
                    responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    description = "Não autenticado.",
                    responseCode = "401",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    description = "Não autorizado.",
                    responseCode = "403",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<Void> remover(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    );
}
