package com.controller.relatorios;

import com.domain.user.Enum.TipoPessoa;
import com.domain.user.relatorio.registroAcesso.AcessoRelatorioFiltro;
import com.services.relatorio.registroAcesso.AcessoReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class RegistroAcessoRelatorioController {

    private final AcessoReportService service;

    @GetMapping(value = "/acessos", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @RequestParam(required = false) TipoPessoa tipoPessoa,
            @RequestParam(required = false) Short codPortaria,
            @RequestParam(required = false, defaultValue = "false") Boolean somenteAbertos,
            @RequestParam(required = false, defaultValue = "true")  Boolean incluirOcupantes,
            @RequestParam(defaultValue = "inline") String disposition // inline | attachment
    ) {
        var filtro = new AcessoRelatorioFiltro(de, ate, tipoPessoa, codPortaria, somenteAbertos, incluirOcupantes);
        byte[] pdf = service.gerarPdf(filtro);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        (disposition.equalsIgnoreCase("attachment") ? "attachment" : "inline")
                                + "; filename=relatorio-acessos-portaria.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

