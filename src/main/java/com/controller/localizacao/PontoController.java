package com.controller.localizacao;

import com.domain.user.endereco.Pontos;
import com.dto.localizacao.PontoDTO;
import com.dto.localizacao.PontosRequestDTO;
import com.repositories.localizacao.PontosRepository;
import com.services.localizacao.PontoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    //@GetMapping(path = {"/listarPontos"})
    //@Override
    //public ResponseEntity<List<Pontos>>listarTodas(){
    //    var lista = PontosRepository.findAll()
    //}

}
