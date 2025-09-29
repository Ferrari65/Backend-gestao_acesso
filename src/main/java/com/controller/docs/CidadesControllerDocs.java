//package com.controller.docs;
//
//import com.domain.user.endereco.Cidade;
//import com.dto.localizacao.CidadeRequestDTO;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.ArraySchema;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.util.List;
//
//public interface CidadesControllerDocs {
//    @Operation(summary = "Listar todas as cidades",
//            description = "Retorna uma lista completa de todas as cidades registradas no sistema.",
//            responses = {
//                    @ApiResponse(
//                            description = "Lista de cidades recuperada com sucesso.",
//                            responseCode = "200",
//                            content = @Content(
//                                    mediaType = "application/json",
//                                    array = @ArraySchema(schema = @Schema(implementation = Cidade.class))
//                            )
//                    ),
//                    @ApiResponse(description = "Nenhuma cidade encontrada.", responseCode = "204", content = @Content),
//                    @ApiResponse(description = "Requisição malformada.", responseCode = "400", content = @Content),
//                    @ApiResponse(description = "Não autenticado. Token de acesso ausente ou inválido.", responseCode = "401", content = @Content),
//                    @ApiResponse(description = "Recurso não encontrado.", responseCode = "404", content = @Content),
//                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
//            }
//    )
//    ResponseEntity<List<Cidade>> listarTodas();
//
//    @Operation(summary = "Criar nova cidade",
//            description = "Cria um novo registro de cidade. Se o nome e a UF já existirem, a operação falhará. Em caso de sucesso, retorna o objeto da cidade criada e o status 201 (Created).",
//            responses = {
//                    @ApiResponse(
//                            description = "Cidade criada com sucesso.",
//                            responseCode = "201",
//                            content = @Content(
//                                    mediaType = "application/json",
//                                    schema = @Schema(implementation = Cidade.class)
//                            )
//                    ),
//                    @ApiResponse(description = "Requisição malformada. Dados ausentes ou em formato incorreto.", responseCode = "400", content = @Content),
//                    @ApiResponse(description = "Não autenticado. Token de acesso ausente ou inválido.", responseCode = "401", content = @Content),
//                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
//            })
//    ResponseEntity<Cidade> criarCidade(@Valid @RequestBody CidadeRequestDTO dto,
//                                       UriComponentsBuilder uriBuilder);
//
//    @Operation(summary = "Atualizar cidade por ID",
//            description = "Atualiza os dados de uma cidade existente, identificada pelo ID. O corpo da requisição deve conter um objeto CidadeRequestDTO com as novas informações. Retorna o objeto da cidade atualizada e o status 200 (OK).",
//            responses = {
//                    @ApiResponse(
//                            description = "Cidade atualizada com sucesso.",
//                            responseCode = "200",
//                            content = @Content(
//                                    mediaType = "application/json",
//                                    schema = @Schema(implementation = Cidade.class)
//                            )
//                    ),
//                    @ApiResponse(description = "Requisição malformada. ID da cidade inválido ou em formato incorreto.", responseCode = "400", content = @Content),
//                    @ApiResponse(description = "Não autenticado. Token de acesso ausente ou inválido.", responseCode = "401", content = @Content),
//                    @ApiResponse(description = "Cidade não encontrada.", responseCode = "404", content = @Content),
//                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
//            })
//    ResponseEntity<?> atualizarCidade(@Parameter(description = "ID da cidade a ser atualizada.") @PathVariable Integer id,
//                                      @Valid @RequestBody CidadeRequestDTO dto);
//
//    @Operation(summary = "Excluir cidade por ID",
//            description = "Exclui permanentemente uma cidade do sistema, identificada pelo seu ID. Esta operação é irreversível.",
//            responses = {
//                    @ApiResponse(description = "Cidade excluída com sucesso.", responseCode = "204", content = @Content),
//                    @ApiResponse(description = "Requisição malformada. ID da cidade inválido ou em formato incorreto.", responseCode = "400", content = @Content),
//                    @ApiResponse(description = "Não autenticado. Token de acesso ausente ou inválido.", responseCode = "401", content = @Content),
//                    @ApiResponse(description = "Cidade não encontrada.", responseCode = "404", content = @Content),
//                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
//            })
//    ResponseEntity<Void> deletarcidade(@Parameter(description = "ID da cidade a ser excluída.") @PathVariable Integer id);
//}