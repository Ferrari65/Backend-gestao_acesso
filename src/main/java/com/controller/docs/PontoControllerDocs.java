package com.controller.docs;

import com.dto.localizacao.Ponto.PontoDTO;
import com.dto.localizacao.Ponto.PontosRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface PontoControllerDocs {

    @Operation(summary = "Listar todos os pontos",
            description = "Retorna uma lista de todos os pontos cadastrados no sistema.",
            responses = {
                    @ApiResponse(
                            description = "Lista de pontos recuperada com sucesso.",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = PontoDTO.class))
                                    )
                            }),
                    @ApiResponse(description = "Nenhum ponto encontrado.", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Requisição malformada.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. O token de acesso está ausente ou é inválido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
            })
    ResponseEntity<List<PontoDTO>> listar();

    @Operation(summary = "Buscar ponto por ID",
            description = "Busca e retorna os detalhes de um ponto específico pelo seu ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            description = "Ponto encontrado e retornado com sucesso.",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = PontoDTO.class)
                                    )
                            }),
                    @ApiResponse(description = "Requisição malformada. ID do ponto inválido ou em formato incorreto.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. O token de acesso está ausente ou é inválido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Ponto não encontrado.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
            })
    ResponseEntity<PontoDTO> buscar(@PathVariable Integer id);


    @Operation(summary = "Atualizar um ponto existente",
            description = "Atualiza completamente as informações de um ponto existente, substituindo os dados com base no ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            description = "Ponto atualizado com sucesso.",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = PontoDTO.class)
                                    )
                            }),
                    @ApiResponse(description = "Requisição malformada. Dados ausentes ou em formato incorreto.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. O token de acesso está ausente ou é inválido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Ponto não encontrado.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
            })
    ResponseEntity<PontoDTO> atualizar(@PathVariable Integer id,
                                       @Valid @RequestBody PontosRequestDTO dto);

    @Operation(summary = "Criar novo ponto",
            description = "Cria um novo ponto com base nas informações fornecidas no corpo da requisição. O ID do novo ponto é gerado automaticamente.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            description = "Ponto criado com sucesso.",
                            responseCode = "201",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = PontoDTO.class)
                                    )
                            }),
                    @ApiResponse(description = "Requisição malformada. Dados ausentes ou em formato incorreto.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. O token de acesso está ausente ou é inválido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
            })
    ResponseEntity<PontoDTO> criar(@Valid @RequestBody PontosRequestDTO dto);

    @Operation(summary = "Remover ponto por ID",
            description = "Exclui permanentemente um ponto do sistema, com base no ID fornecido. Esta é uma operação irreversível.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(description = "Ponto deletado com sucesso. Nenhum conteúdo retornado.", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Requisição malformada. ID do ponto inválido ou em formato incorreto.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. O token de acesso está ausente ou é inválido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Ponto não encontrado.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
            })
    ResponseEntity<Void> excluir(@PathVariable Integer id);
}