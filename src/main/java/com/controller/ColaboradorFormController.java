package com.controller;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;
import com.domain.user.endereco.Cidade;
import com.dto.colaborador.FormCreateRequest;
import com.dto.colaborador.FormResponse;
import com.dto.colaborador.FormStatusUpdateRequest;
import com.services.AuthorizationService;
import com.services.colaborador.ColaboradorFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Colaborador-Form",
    description = "Gerenciar Formulários de Colaboradores: Endpoint para criar, ler, atualizar e excluir formulários de colaboradores.")
public class ColaboradorFormController {

    private final ColaboradorFormService service;
    private final AuthorizationService auth;

    @PostMapping("/me/form")
    @PreAuthorize("hasAnyRole('COLABORADOR','LIDER','GESTOR')")
    @Operation(summary = "Cadastrar rota preferida do usuário",
               description = "Permite que colaboradores, líderes e gestores salvem sua rota preferida para otimizar o fluxo de trabalho.",
            responses = {
                    @ApiResponse(
                            description = "Formulario cadastrado com sucesso.",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Cidade.class))
                            )
                    ),
                    @ApiResponse(description = "Requisição malformada.", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Não autenticado. Token de acesso ausente ou inválido.", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Recurso não encontrado.", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro interno do servidor. Ocorreu um problema inesperado.", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<FormResponse> criarMeuForm(@RequestBody FormCreateRequest req) {
        UUID idColab = auth.getCurrentUserId();
        var resp = service.criarPara(idColab, req);
        return ResponseEntity.ok(resp);
    }

//    @PostMapping("/colaboradores/{idColaborador}/form")
//    @PreAuthorize("hasRole('GESTOR')")
//    public ResponseEntity<FormResponse> criarEmNomeDe(
//            @PathVariable UUID idColaborador,
//            @RequestBody FormCreateRequest req) {
//        var resp = service.criarPara(idColaborador, req);
//        return ResponseEntity.ok(resp);
//    }

//    @GetMapping("/me/form")
//    @PreAuthorize("hasAnyRole('COLABORADOR','LIDER','GESTOR')")
//    public List<FormResponse> meusForms() {
//        UUID idColab = auth.getCurrentUserId();
//        return service.meusFormularios(idColab);
//    }

    @GetMapping("/forms")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Listagem de formulario filtrado")
    public List<FormResponse> listar(
            @RequestParam(required = false) Integer idCidade,
            @RequestParam(required = false) Periodo turno,
            @RequestParam(required = false) StatusForm status) {
        return service.listar(idCidade, turno, status);
    }

    @PatchMapping("/forms/{idForm}/status")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Atualizar Status do Formulário",
               description = "Exclusivo para gestores, este endpoint permite alterar o status de um formulário existente, por exemplo, de PENDENTE para APROVADO.")
    public ResponseEntity<FormResponse> atualizarStatus(
            @PathVariable UUID idForm,
            @RequestBody FormStatusUpdateRequest req) {
        var resp = service.atualizarStatus(idForm, req.status());
        return ResponseEntity.ok(resp);
    }
}
