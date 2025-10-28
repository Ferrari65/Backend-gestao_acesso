package com.controller;

import com.dto.impedimentos.ImpedimentoCreateRequest;
import com.dto.impedimentos.ImpedimentoResponse;
import com.services.impedimento.ImpedimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

    @RestController
    @RequestMapping("/impedimentos")
    @RequiredArgsConstructor
    public class ImpedimentoController {

        private final ImpedimentoService service;

        @PostMapping
        public ResponseEntity<ImpedimentoResponse> criar(@RequestBody @Valid ImpedimentoCreateRequest req) {
            var resp = service.criar(req);
            return ResponseEntity.created(URI.create("/api/impedimentos/" + resp.id())).body(resp);
        }

        @GetMapping
        public List<ImpedimentoResponse> listar(@RequestParam(value = "ativos", required = false) Boolean apenasAtivos) {
            return service.listar(apenasAtivos);
        }

        @PatchMapping("/{id}/inativar")
        public ImpedimentoResponse inativar(@PathVariable UUID id) {
            return service.inativar(id);
        }
    }
