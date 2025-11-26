package com.controller.relatorios;

import com.dto.mapa.MapaColabPontoDTO;
import com.services.colaborador.RotaColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/MapasTrackpass")
@Tag(name = "Mapas", description = "Endpoints para informações de mapa")
public class MapaController {

    private final RotaColaboradorService service;

    @GetMapping("/mapaRotaColaborador")
    @Operation(summary = "Mapa de colaboradores por ponto")
    public ResponseEntity<List<MapaColabPontoDTO>> mapa(
            @RequestParam(required = false) Integer idRota
    ) {
        return ResponseEntity.ok(service.mapaColaboradores(idRota));
    }
}
