package com.controller;

import com.dto.registroEmbarque.RegistrarEmbarqueRequest;
import com.dto.registroEmbarque.RegistroEmbarqueResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viagens/{idViagem}/embarques")
@RequiredArgsConstructor
public class RegistroEmbarqueController {

    private final com.services.registroEmbarque.RegistroEmbarqueService service;

    @PostMapping
    public ResponseEntity<RegistroEmbarqueResponse> registrar(
            @PathVariable UUID idViagem,
            @RequestBody RegistrarEmbarqueRequest request,
            @AuthenticationPrincipal(expression = "id") UUID idValidador
    ) {
        var resp = service.registrar(idViagem, request, idValidador);
        return ResponseEntity
                .created(URI.create("/viagens/" + idViagem + "/embarques/" + resp.getIdEmbarque()))
                .body(resp);

    }

    @GetMapping
    public ResponseEntity<List<RegistroEmbarqueResponse>> listarTodos(@PathVariable UUID idViagem) {
        return ResponseEntity.ok(service.listarTodos(idViagem));
    }

}