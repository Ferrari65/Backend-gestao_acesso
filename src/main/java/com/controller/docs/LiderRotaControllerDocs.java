package com.controller.docs;

import com.dto.liderRota.LiderRotaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface LiderRotaControllerDocs {

    @Operation(
            operationId = "atribuirLiderARota",
            summary = "Atribuir líder a uma rota",
            description = "Vincula um colaborador existente como líder da rota informada. " +
                    "Operação idempotente: replays não criam duplicatas.",
            parameters = {
                    @Parameter(name = "idRota", in = ParameterIn.PATH, required = true,
                            description = "Identificador numérico da rota.", example = "42"),
                    @Parameter(name = "idColaborador", in = ParameterIn.PATH, required = true,
                            description = "UUID do colaborador a ser atribuído como líder.",
                            example = "55efc89a-e69c-4020-a853-e292885f736b")
            },
            responses = {
                    @ApiResponse(
                            description = "Líder atribuído à rota com sucesso.",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LiderRotaResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Requisição inválida. IDs malformados ou regra de negócio violada.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. Forneça um token de acesso válido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não autorizado.", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Rota ou colaborador não localizado.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno ao processar a atribuição.", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<LiderRotaResponse> atribuir(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    );

    @Operation(
            operationId = "listarLideresDaRota",
            summary = "Listar líderes da rota",
            description = "Retorna a relação de líderes atualmente vinculados à rota especificada.",
            parameters = {
                    @Parameter(name = "idRota", in = ParameterIn.PATH, required = true,
                            description = "Identificador numérico da rota.", example = "42")
            },
            responses = {
                    @ApiResponse(
                            description = "Lista de líderes retornada com sucesso.",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LiderRotaResponse.class))
                            )
                    ),
                    @ApiResponse(description = "A rota não possui líderes vinculados no momento.", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Parâmetros inválidos ou formato de rota incorreto.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. Forneça um token de acesso válido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Rota não localizada.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno ao recuperar a lista de líderes.", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<LiderRotaResponse>> listar(@PathVariable Integer idRota);

    @Operation(
            operationId = "removerLiderDaRota",
            summary = "Remover líder da rota",
            description = "Desvincula o colaborador (líder) da rota informada.",
            responses = {
                    @ApiResponse(
                            description = "Líder removido com sucesso. Nenhum conteúdo retornado.",
                            responseCode = "204",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LiderRotaResponse.class))
                            )),
                    @ApiResponse(description = "IDs inválidos ou formato incorreto.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. Forneça um token de acesso válido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não autorizado.", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Rota ou líder não localizado.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno ao processar a remoção.", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> remover(
            @PathVariable Integer idRota,
            @PathVariable UUID idColaborador
    );
}
