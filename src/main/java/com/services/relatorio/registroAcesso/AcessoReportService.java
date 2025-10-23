package com.services.relatorio.registroAcesso;

import com.domain.user.Enum.TipoPessoa;
import com.domain.user.registroAcesso.RegistroAcesso;
import com.domain.user.relatorio.registroAcesso.AcessoRelatorioFiltro;
import com.domain.user.relatorio.registroAcesso.AcessoRelatorioRow;
import com.dto.registroAcesso.AcessoResponse;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.repositories.UserRepository;
import com.repositories.registroAcesso.RegistroAcessoOcupanteRepository;
import com.repositories.registroAcesso.RegistroAcessoRepository;
import com.repositories.visitante.VisitanteRepository;
import com.services.registroAcesso.AcessoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcessoReportService {

    private final AcessoService acessoService;
    private final TemplateEngine templateEngine;

    public byte[] gerarPdf(LocalDate de, LocalDate ate,
                           Boolean abertos, Short codPortaria, String tipoPessoaStr) {

        // 🔹 buscar dados reais do banco via AcessoService
        List<AcessoResponse> linhas;
        if (abertos != null && abertos) {
            linhas = acessoService.listarAbertos();
        } else if (codPortaria != null) {
            linhas = acessoService.listarHistoricoSomentePortaria(codPortaria);
        } else if (tipoPessoaStr != null) {
            TipoPessoa tipo = TipoPessoa.valueOf(tipoPessoaStr.toUpperCase());
            linhas = acessoService.listarHistoricoSomenteTipo(tipo);
        } else {
            linhas = acessoService.listarHistoricoPorData(de, ate);
        }

        // 🔹 preparar o contexto Thymeleaf
        Context ctx = new Context();
        ctx.setVariable("linhas", linhas);
        ctx.setVariable("filtro", new Object() {
            public LocalDate de() { return de; }
            public LocalDate ate() { return ate; }
            public Boolean somenteAbertos() { return abertos; }
            public Short codPortaria() { return codPortaria; }
            public String tipoPessoa() { return tipoPessoaStr; }
            public Boolean incluirOcupantes() { return true; }
        });
        ctx.setVariable("totaisGeral", linhas.size());
        ctx.setVariable("totaisAbertos", linhas.stream().filter(l -> l.saida() == null).count());
        ctx.setVariable("totaisFinalizados", linhas.stream().filter(l -> l.saida() != null).count());
        ctx.setVariable("geradoEm", OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")));

        // 🔹 renderizar o HTML usando o template real
        String html = templateEngine.process("relatorios/acessos-portaria", ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }
}