package com.controller.docs;

import com.dto.veiculo.VeiculoRequestDTO;
import com.dto.veiculo.VeiculoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface VeiculoControllerDocs {


    @Operation(
            summary = "Cria um novo veículo",
            description = "Cria um veículo ativo. A placa é opcional, porém deve ser única entre veículos ativos."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Veículo criado",
            content = @Content(schema = @Schema(implementation = VeiculoResponseDTO.class))
    )
    @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content)
    @ApiResponse(responseCode = "409", description = "Placa já utilizada por veículo ativo", content = @Content)
    ResponseEntity<VeiculoResponseDTO> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação de veículo",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VeiculoRequestDTO.class))
            )
            @RequestBody @Valid VeiculoRequestDTO dto);


    @Operation(
            summary = "Lista veículos ativos",
            description = "Retorna todos os veículos com ativo=true."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de veículos ativos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = VeiculoResponseDTO.class)))
    )
    ResponseEntity<List<VeiculoResponseDTO>> listar();


    @Operation(
            summary = "Busca veículo por ID",
            description = "Retorna os detalhes de um veículo ativo a partir do seu ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Veículo encontrado",
            content = @Content(schema = @Schema(implementation = VeiculoResponseDTO.class))
    )
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado ou inativo", content = @Content)
    ResponseEntity<VeiculoResponseDTO> buscar(
            @Parameter(description = "ID do veículo", example = "1")
            @PathVariable Long id);


    @Operation(
            summary = "Atualiza veículo",
            description = "Atualiza os dados de um veículo ativo existente."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Veículo atualizado",
            content = @Content(schema = @Schema(implementation = VeiculoResponseDTO.class))
    )
    @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado ou inativo", content = @Content)
    @ApiResponse(responseCode = "409", description = "Placa já utilizada por veículo ativo", content = @Content)
    ResponseEntity<VeiculoResponseDTO> atualizar(
            @Parameter(description = "ID do veículo a atualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para atualização de veículo",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VeiculoRequestDTO.class))
            )
            @RequestBody @Valid VeiculoRequestDTO dto);

    @Operation(
            summary = "Desativa veículo",
            description = "Marca o veículo como ativo=false. Preserva o histórico, mas o veículo deixa de aparecer nas listagens ativas."
    )
    @ApiResponse(responseCode = "204", description = "Veículo desativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content)
    ResponseEntity<Void> desativar(
            @Parameter(description = "ID do veículo", example = "1")
            @PathVariable Long id);

    @Operation(
            summary = "Reativa veículo",
            description = "Marca o veículo como ativo=true. Falha se já houver outro veículo ativo com a mesma placa."
    )
    @ApiResponse(responseCode = "204", description = "Veículo reativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Placa já utilizada por outro veículo ativo", content = @Content)
    ResponseEntity<Void> reativar(
            @Parameter(description = "ID do veículo", example = "1")
            @PathVariable Long id);
}
