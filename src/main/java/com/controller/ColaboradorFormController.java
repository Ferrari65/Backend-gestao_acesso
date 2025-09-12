package com.controller;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;
import com.dto.colaborador.FormCreateRequest;
import com.dto.colaborador.FormResponse;
import com.dto.colaborador.FormStatusUpdateRequest;
import com.services.AuthorizationService;
import com.services.colaborador.ColaboradorFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ColaboradorFormController {

    private final ColaboradorFormService service;
    private final AuthorizationService auth;

    @PostMapping("/me/form")
    @PreAuthorize("hasAnyRole('COLABORADOR','LIDER','GESTOR')")
    public ResponseEntity<FormResponse> criarMeuForm(@RequestBody FormCreateRequest req) {
        UUID idColab = auth.getCurrentUserId();
        var resp = service.criarPara(idColab, req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/colaboradores/{idColaborador}/form")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<FormResponse> criarEmNomeDe(
            @PathVariable UUID idColaborador,
            @RequestBody FormCreateRequest req) {
        var resp = service.criarPara(idColaborador, req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me/form")
    @PreAuthorize("hasAnyRole('COLABORADOR','LIDER','GESTOR')")
    public List<FormResponse> meusForms() {
        UUID idColab = auth.getCurrentUserId();
        return service.meusFormularios(idColab);
    }

    @GetMapping("/forms")
    @PreAuthorize("hasRole('GESTOR')")
    public List<FormResponse> listar(
            @RequestParam(required = false) Integer idCidade,
            @RequestParam(required = false) Periodo turno,
            @RequestParam(required = false) StatusForm status) {
        return service.listar(idCidade, turno, status);
    }

    @PatchMapping("/forms/{idForm}/status")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<FormResponse> atualizarStatus(
            @PathVariable UUID idForm,
            @RequestBody FormStatusUpdateRequest req) {
        var resp = service.atualizarStatus(idForm, req.status());
        return ResponseEntity.ok(resp);
    }
}
