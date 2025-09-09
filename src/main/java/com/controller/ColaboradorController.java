package com.controller;

import com.dto.colaborador.ColaboradorDTO;
import com.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/listarcolaboradores")
@RequiredArgsConstructor

@Tag(name="Colaboradores", description = "Endpoints de Colaborador")
public class ColaboradorController {
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('GESTOR')")
    @GetMapping
    @Operation(summary = "Listar todos colaboradores",
            description = "Listar todos colaboradores",
        responses = {
            @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ColaboradorDTO.class))
                            )
                    }),
                @ApiResponse (description = "No Content", responseCode = "204" , content = @Content),
                @ApiResponse (description = "Bad Request", responseCode = "400", content = @Content),
                @ApiResponse (description = "Unauthorized", responseCode = "401", content = @Content),
                @ApiResponse (description = "Not Found", responseCode = "404", content = @Content),
                @ApiResponse (description = "Internal Server Error", responseCode = "500",content = @Content)
        }
    )
    public List<ColaboradorDTO> listarColaborador(){
        return userRepository.findAll().stream().map(ColaboradorDTO::from).toList();
    }
}
