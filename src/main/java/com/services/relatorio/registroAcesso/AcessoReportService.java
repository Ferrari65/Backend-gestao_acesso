package com.services.relatorio.registroAcesso;

import com.domain.user.Enum.TipoPessoa;
import com.domain.user.registroAcesso.RegistroAcesso;
import com.domain.user.relatorio.registroAcesso.AcessoRelatorioFiltro;
import com.domain.user.relatorio.registroAcesso.AcessoRelatorioRow;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.repositories.UserRepository;
import com.repositories.registroAcesso.RegistroAcessoOcupanteRepository;
import com.repositories.registroAcesso.RegistroAcessoRepository;
import com.repositories.visitante.VisitanteRepository;
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

    private final RegistroAcessoRepository registroRepo;
    private final RegistroAcessoOcupanteRepository ocupanteRepo;
    private final UserRepository userRepo;
    private final VisitanteRepository visitanteRepo;
    private final TemplateEngine templateEngine;

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    @Transactional(readOnly = true)
    public byte[] gerarPdf(AcessoRelatorioFiltro filtro) {

        LocalDate hoje = LocalDate.now(ZONE_ID);
        LocalDate de  = (filtro.de()  != null) ? filtro.de()  : hoje.minusDays(7);
        LocalDate ate = (filtro.ate() != null) ? filtro.ate() : hoje;
        if (ate.isBefore(de)) throw new IllegalArgumentException("'ate' não pode ser anterior a 'de'.");

        OffsetDateTime inicioLocal      = de.atStartOfDay(ZONE_ID).toOffsetDateTime();
        OffsetDateTime fimExclusivoLocal= ate.plusDays(1).atStartOfDay(ZONE_ID).toOffsetDateTime();
        OffsetDateTime inicioUTC        = inicioLocal.withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime fimExclusivoUTC  = fimExclusivoLocal.withOffsetSameInstant(ZoneOffset.UTC);

        List<RegistroAcesso> base = registroRepo
                .findByEntradaGreaterThanEqualAndEntradaLessThanOrderByEntradaDesc(inicioUTC, fimExclusivoUTC);

        var stream = base.stream();
        if (filtro.tipoPessoa() != null) {
            stream = stream.filter(r -> r.getTipoPessoa() == filtro.tipoPessoa());
        }
        if (filtro.codPortaria() != null) {
            stream = stream.filter(r -> filtro.codPortaria().equals(r.getCodPortaria()));
        }
        if (filtro.isSomenteAbertos()) {
            stream = stream.filter(r -> r.getSaida() == null);
        }

        var regs = stream.sorted(Comparator.comparing(RegistroAcesso::getEntrada).reversed()).toList();
        var linhas = regs.stream().map(r -> {
            var entradaZ = r.getEntrada() != null ? r.getEntrada().atZoneSameInstant(ZONE_ID) : null;
            var saidaZ   = r.getSaida() != null ? r.getSaida().atZoneSameInstant(ZONE_ID) : null;

            String condutorNome = resolverNomeCondutor(r.getTipoPessoa(), r.getIdPessoa());
            String condutorId   = r.getIdPessoa() != null ? r.getIdPessoa().toString() : "-";

            String ocupantesStr = "";
            if (filtro.isIncluirOcupantes()) {
                var ocupantes = ocupanteRepo.findByRegistroId(r.getId()).stream()
                        .map(oc -> resolverNomeColaborador(oc.getIdColaborador()))
                        .toList();
                ocupantesStr = String.join("; ", ocupantes);
            }

            return new AcessoRelatorioRow(
                    r.getTipoPessoa(),
                    condutorNome,
                    condutorId,
                    r.getCodPortaria(),
                    entradaZ != null ? entradaZ.toLocalDate() : null,
                    entradaZ != null ? entradaZ.toLocalTime() : null,
                    saidaZ   != null ? saidaZ.toLocalDate()   : null,
                    saidaZ   != null ? saidaZ.toLocalTime()   : null,
                    r.getSaida() == null ? "ABERTO" : "FINALIZADO",
                    ocupantesStr,
                    r.getObservacao()
            );
        }).toList();

        var ctx = new Context();
        ctx.setVariable("filtro", filtro);
        ctx.setVariable("linhas", linhas);
        ctx.setVariable("geradoEm", OffsetDateTime.now(ZONE_ID));
        ctx.setVariable("totaisAbertos", linhas.stream().filter(l -> "ABERTO".equals(l.situacao())).count());
        ctx.setVariable("totaisFinalizados", linhas.stream().filter(l -> "FINALIZADO".equals(l.situacao())).count());
        ctx.setVariable("totaisGeral", linhas.size());

        String html = templateEngine.process("relatorios/acessos-portaria", ctx);

        try (var out = new ByteArrayOutputStream()) {
            new PdfRendererBuilder()
                    .withHtmlContent(html, getBaseUrl())
                    .toStream(out)
                    .run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }

    private String resolverNomeCondutor(TipoPessoa tipo, UUID id) {
        if (id == null) return "-";
        return switch (tipo) {
            case COLABORADOR -> userRepo.findResumoByIdColaborador(id)
                    .map(res -> res.getNome())
                    .orElse(id.toString());
            case VISITANTE -> visitanteRepo.findById(id)
                    .map(v -> v.getNomeCompleto())
                    .orElse(id.toString());
        };
    }
    private String resolverNomeColaborador(UUID id) {
        if (id == null) return "-";
        return userRepo.findResumoByIdColaborador(id)
                .map(res -> res.getNome())
                .orElse(id.toString());
    }

    private String getBaseUrl() {
        return getClass().getResource("/templates/").toExternalForm();
    }
}