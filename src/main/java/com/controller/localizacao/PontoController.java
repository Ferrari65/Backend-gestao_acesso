package com.controller.localizacao;

import com.dto.localizacao.PontoDTO;
import com.dto.localizacao.PontosRequestDTO;
import com.services.localizacao.PontoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('GESTOR')")
@RequestMapping("/pontos")
public class PontoController {
    private final PontoService service;

    @PostMapping("/cadastrarPontos")
    @Tag(name="Pontos", description = "Endpoints destinado ao CRUD de Pontos")
    public ResponseEntity<PontoDTO> criar(@RequestBody PontosRequestDTO dto){
        var salvo = service.criar(dto);
        return  ResponseEntity.ok(PontoDTO.from(salvo));
    }

}
