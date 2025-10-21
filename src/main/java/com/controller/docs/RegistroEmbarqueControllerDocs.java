package com.controller.docs;

import com.dto.registroEmbarque.RegistrarEmbarqueRequest;
import com.dto.registroEmbarque.RegistroEmbarqueResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface RegistroEmbarqueControllerDocs {

    @Operation(summary = "Realizar registro de embarque")
    ResponseEntity<RegistroEmbarqueResponse> registrar(
            @PathVariable UUID idViagem,
            @RequestBody RegistrarEmbarqueRequest request,
            @AuthenticationPrincipal(expression = "id") UUID idValidador
    );

    @Operation(summary = "Lista todos os registro de Embarque por Viagem")
    ResponseEntity<List<RegistroEmbarqueResponse>> listarTodos(@PathVariable UUID idViagem);
}
