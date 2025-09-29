//package com.controller;
//
//import com.dto.veiculo.VeiculoRequestDTO;
//import com.dto.veiculo.VeiculoResponseDTO;
//import com.services.veiculo.VeiculoService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.ArraySchema;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.List;
//
//@RestController
//@RequestMapping("/veiculos")
//@RequiredArgsConstructor
//@Tag(name = "Veículos", description = "Operações de cadastro, consulta e gestão de veículos")
//public class VeiculoController implements com.controller.docs.VeiculoControllerDocs {
//
//    private final VeiculoService service;
//
//    @PostMapping
//    @PreAuthorize("hasRole('GESTOR')")
//    @Override
//    public ResponseEntity<VeiculoResponseDTO> criar(
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    description = "Dados para criação de veículo",
//                    required = true,
//                    content = @Content(schema = @Schema(implementation = VeiculoRequestDTO.class))
//            )
//            @RequestBody @Valid VeiculoRequestDTO dto) {
//
//        VeiculoResponseDTO resp = service.criar(dto);
//        return ResponseEntity.created(URI.create("/veiculos/" + resp.id())).body(resp);
//    }
//
//    @GetMapping
//    @PreAuthorize("hasRole('GESTOR')")
//    @Override
//    public ResponseEntity<List<VeiculoResponseDTO>> listar() {
//        return ResponseEntity.ok(service.listarAtivos());
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('GESTOR')")
//    @Override
//    public ResponseEntity<VeiculoResponseDTO> buscar(
//            @Parameter(description = "ID do veículo", example = "1")
//            @PathVariable Long id) {
//        return ResponseEntity.ok(service.buscarPorId(id));
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('GESTOR')")
//    @Override
//    public ResponseEntity<VeiculoResponseDTO> atualizar(
//            @Parameter(description = "ID do veículo a atualizar", example = "1")
//            @PathVariable Long id,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    description = "Dados para atualização de veículo",
//                    required = true,
//                    content = @Content(schema = @Schema(implementation = VeiculoRequestDTO.class))
//            )
//            @RequestBody @Valid VeiculoRequestDTO dto) {
//        return ResponseEntity.ok(service.atualizar(id, dto));
//    }
//
//    @PatchMapping("/{id}/desativar")
//    @PreAuthorize("hasRole('GESTOR')")
//    @Override
//    public ResponseEntity<Void> desativar(
//            @Parameter(description = "ID do veículo", example = "1")
//            @PathVariable Long id) {
//        service.desativar(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/reativar")
//    @PreAuthorize("hasRole('GESTOR')")
//    @Override
//    public ResponseEntity<Void> reativar(
//            @Parameter(description = "ID do veículo", example = "1")
//            @PathVariable Long id) {
//        service.reativar(id);
//        return ResponseEntity.noContent().build();
//    }
//}
