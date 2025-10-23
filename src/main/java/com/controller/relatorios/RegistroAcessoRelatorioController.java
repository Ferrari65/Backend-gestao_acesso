package com.controller.relatorios;

import com.domain.user.Enum.TipoPessoa;
import com.domain.user.relatorio.registroAcesso.AcessoRelatorioFiltro;
import com.services.relatorio.registroAcesso.AcessoReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    private final AcessoReportService reportService;

    @Operation(summary = "Relatório de acessos (PDF)")
    @ApiResponse(
            responseCode = "200",
            description = "PDF gerado",
            content = @Content(
                    mediaType = "application/pdf",
                    schema = @Schema(type = "string", format = "binary")
            )
    )
    @GetMapping(value = "/relatorios/acessos", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerar(
            @RequestParam(required = false) LocalDate de,
            @RequestParam(required = false) LocalDate ate,
            @RequestParam(required = false) Boolean abertos,
            @RequestParam(required = false) Short codPortaria,
            @RequestParam(required = false) String tipoPessoa
    ) {
        byte[] pdf = reportService.gerarPdf(de, ate, abertos, codPortaria, tipoPessoa);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-acessos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}