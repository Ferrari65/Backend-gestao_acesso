package com.controller.docs;

import com.dto.PATCH.RotaPatchDTO;
import com.dto.localizacao.Rota.RotaDTO;
import com.dto.localizacao.Rota.RotaListDTO;
import com.dto.localizacao.Rota.RotaPontoItemDTO;
import com.dto.localizacao.Rota.RotaRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public interface RotaControllerDocs {

    @Operation(
            summary = "Buscar todas as rotas",
            description = "Retorna uma lista de todas as rotas cadastradas. A lista pode estar vazia (status 204).",
            responses = {
                    @ApiResponse(
                            description = "Lista de rotas recuperada com sucesso.",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = RotaDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "Nenhuma rota encontrada.", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Requisição inválida.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Autenticação necessária.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não autorizado. O usuário não tem permissão para acessar este recurso.", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor.", responseCode = "500", content = @Content)
            })
    ResponseEntity<List<RotaListDTO>> listar();

//    @Operation(
//            summary = "Buscar rota por ID",
//            description = "Busca e retorna os detalhes de uma rota específica pelo seu ID.",
//            responses = {
//                    @ApiResponse(
//                            description = "Rota encontrada e retornada com sucesso.",
//                            responseCode = "200",
//                            content = {
//                                    @Content(
//                                            mediaType = "application/json",
//                                            schema = @Schema(implementation = RotaDTO.class)
//                                    )
//                            }),
//                    @ApiResponse(description = "Requisição inválida (ID inválido).", responseCode = "400", content = @Content),
//                    @ApiResponse(description = "Autenticação necessária.", responseCode = "401", content = @Content),
//                    @ApiResponse(description = "Rota não encontrada.", responseCode = "404", content = @Content),
//                    @ApiResponse(description = "Erro interno do servidor.", responseCode = "500", content = @Content)
//            })
//    ResponseEntity<RotaDTO> buscar(@Parameter(description = "ID da rota a ser buscada.", example = "1") @PathVariable Integer id);

    @Operation(
            summary = "Criar nova rota",
            description = "Cria uma nova rota com base nos dados fornecidos e retorna a URL do recurso criado.",
            responses = {
                    @ApiResponse(
                            description = "Rota criada com sucesso.",
                            responseCode = "201",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = RotaDTO.class)
                                    )
                            }),
                    @ApiResponse(description = "Requisição inválida (dados ausentes ou incorretos).", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Autenticação necessária.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não autorizado. O usuário não tem permissão para criar uma rota.", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor.", responseCode = "500", content = @Content)
            })
    ResponseEntity<RotaDTO> criar(@Valid @RequestBody RotaRequestDTO dto,
                                  UriComponentsBuilder uri);

    @Operation(
            summary = "Atualizar rota completa",
            description = "Substitui completamente uma rota existente pelo ID. Todos os campos, incluindo a lista de pontos, são substituídos.",
            responses = {
                    @ApiResponse(
                            description = "Rota atualizada com sucesso.",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = RotaDTO.class)
                                    )
                            }),
                    @ApiResponse(description = "Requisição inválida (ID ou dados incorretos).", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Autenticação necessária.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não autorizado. O usuário não tem permissão para atualizar uma rota.", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Rota não encontrada.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor.", responseCode = "500", content = @Content)
            })
    ResponseEntity<RotaDTO> atualizar(@Parameter(description = "ID da rota a ser atualizada.", example = "1") @PathVariable Integer id,
                                      @Valid @RequestBody RotaRequestDTO dto);

    @Operation(
            summary = "Atualizar rota parcialmente (PATCH)",
            description = "Atualiza parcialmente uma rota existente, alterando apenas os campos fornecidos no corpo da requisição.",
            responses = {
                    @ApiResponse(
                            description = "Rota atualizada parcialmente com sucesso.",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = RotaDTO.class)
                                    )
                            }),
                    @ApiResponse(description = "Requisição inválida (dados incorretos).", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Autenticação necessária.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não autorizado. O usuário não tem permissão para atualizar uma rota.", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Rota não encontrada.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor.", responseCode = "500", content = @Content)
            })
    ResponseEntity<RotaDTO> patch(@Parameter(description = "ID da rota a ser atualizada parcialmente.", example = "1") @PathVariable Integer id,
                                  @RequestBody RotaPatchDTO dto);


    @Operation(
            summary = "Deletar rota",
            description = "Remove uma rota permanentemente pelo seu ID.",
            responses = {
                    @ApiResponse(description = "Rota deletada com sucesso.", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Requisição inválida (ID inválido).", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Autenticação necessária.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não autorizado. O usuário não tem permissão para deletar uma rota.", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Rota não encontrada.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor.", responseCode = "500", content = @Content)
            })
    ResponseEntity<Void> deletar(@Parameter(description = "ID da rota a ser deletada.", example = "1") @PathVariable Integer idRota);


    @Operation(
            summary = "Obter trajeto da rota",
            description = "Retorna a sequência de pontos (com latitude/longitude) para desenhar o percurso no mapa.**LIDER** **COLABORADOR**"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trajeto retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RotaPontoItemDTO.class)))),
            @ApiResponse(responseCode = "204", description = "Rota sem pontos cadastrados"),
            @ApiResponse(responseCode = "404", description = "Rota não encontrada")
    })
    ResponseEntity<List<RotaPontoItemDTO>> trajeto(@PathVariable Integer idRota);

}
