package com.controller.localizacao.docs;

import com.domain.user.endereco.Cidade;
import com.dto.localizacao.CidadeRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public interface CidadesControllerDocs {
    @Operation(summary = "Listar todas as Cidades",
            description = "Retorna uma lista completa de todas as cidades registradas no sistema.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "aplication/json",
                                            array = @ArraySchema(schema = @Schema(implementation = CidadeRequestDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<Cidade>> listarTodas();

    @Operation(summary = "Cadastrar Cidades",
            description = "Permite o cadastro de uma nova cidade, validando se o nome e a UF (Unidade Federativa) já existem.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "aplication/json",
                                            array = @ArraySchema(schema = @Schema(implementation = CidadeRequestDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    ResponseEntity<Cidade> criarCidade(@Valid @RequestBody CidadeRequestDTO dto,
                                       UriComponentsBuilder uriBuilder);

    @Operation(summary = "Atualizar Cidade",
            description = "Permite atualizar os dados (nome e UF) de uma cidade específica, identificada pelo seu ID.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "aplication/json",
                                            array = @ArraySchema(schema = @Schema(implementation = CidadeRequestDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    ResponseEntity<?> atualizarCidade(@PathVariable Integer id,
                                      @Valid @RequestBody CidadeRequestDTO dto);

    @Operation(summary = "Deletar Cidade",
            description = "Permite a remoção de uma cidade específica do sistema, identificada por seu ID único.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "aplication/json",
                                            array = @ArraySchema(schema = @Schema(implementation = CidadeRequestDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    ResponseEntity<Void> deletarcidade(@PathVariable Integer id);
}
