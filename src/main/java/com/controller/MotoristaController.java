//package com.controller;
//
//import com.dto.Motorista.MotoristaResponseDTO;
//import com.services.motorista.MotoristaService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.ArraySchema;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/motorista")
//@RequiredArgsConstructor
//@Tag( name = "Motoristas",
//        description = "Endpoints para gerenciamento de Motorista")
//public class MotoristaController {
//
//    private final MotoristaService service;
//
//    @GetMapping
//    @Operation(
//            summary = "Lista motoristas ativos",
//            description = "Retorna todos os motoristas com ativo=true, incluindo nome, CNH, telefone, empresa terceirizada e validade da CNH."
//    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "Lista de motoristas ativos",
//            content = @Content(
//                    array = @ArraySchema(
//                            schema = @Schema(implementation = MotoristaResponseDTO.class)))
//    )
//    public ResponseEntity<List<MotoristaResponseDTO>> listar() {
//        return ResponseEntity.ok(service.listarAtivos());
//    }
//}
