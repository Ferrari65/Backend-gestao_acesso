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

    @Operation(summary = "Criar uma nova Cidade",
            description = "Cria um novo registro de cidade no sistema. O corpo da requisição deve conter o nome e a UF da cidade. Se o nome e a UF já existirem, a operação falhará. Em caso de sucesso, retorna o objeto da cidade criada e o status 201 (Created).",
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

    @Operation(summary = "Atualizar uma Cidade por ID",
            description = "Atualiza os dados de uma cidade existente, identificada pelo ID fornecido no caminho da URL. O corpo da requisição deve conter o objeto CidadeRequestDTO com as novas informações. Retorna o objeto da cidade atualizada e o status 200 (OK).",
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

    @Operation(summary = "Excluir uma Cidade por ID",
            description = "Exclui permanentemente uma cidade do sistema, identificada pelo seu ID. Esta operação é irreversível. Em caso de sucesso, retorna o status 204 (No Content).",
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
