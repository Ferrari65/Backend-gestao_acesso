package com.services.registroAcesso;

import com.domain.user.Enum.TipoPessoa;
import com.domain.user.registroAcesso.RegistroAcesso;
import com.domain.user.registroAcesso.RegistroAcessoOcupante;
import com.dto.registroAcesso.*;
import com.repositories.UserRepository;
import com.repositories.registroAcesso.RegistroAcessoOcupanteRepository;
import com.repositories.registroAcesso.RegistroAcessoRepository;
import com.repositories.visitante.VisitanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcessoService {

    private static final Set<Short> PORTARIAS_VALIDAS =
            Set.of((short)1, (short)2, (short)3, (short)4, (short)5);

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private final RegistroAcessoRepository registroRepo;
    private final RegistroAcessoOcupanteRepository ocupanteRepo;
    private final UserRepository userRepo;
    private final VisitanteRepository visitanteRepo;

    @Transactional
    public AcessoResponse criar(AcessoCreateRequest req) {
        Objects.requireNonNull(req.tipoPessoa(), "tipoPessoa é obrigatório");
        Objects.requireNonNull(req.idPessoa(), "idPessoa é obrigatório");
        Objects.requireNonNull(req.codPortaria(), "codPortaria é obrigatório");
        if (!PORTARIAS_VALIDAS.contains(req.codPortaria())) {
            throw new IllegalArgumentException("Portaria inválida");
        }

        validarPessoaExiste(req.tipoPessoa(), req.idPessoa());

        registroRepo.findAbertoDoCondutor(req.idPessoa(), req.tipoPessoa())
                .ifPresent(r -> { throw new IllegalStateException("Já existe um acesso aberto para este condutor."); });

        List<UUID> ocupantesIds = Optional.ofNullable(req.ocupantes()).orElseGet(List::of);

        if (ocupantesIds.stream().anyMatch(id -> id.equals(req.idPessoa()))) {
            throw new IllegalArgumentException("O condutor não pode ser ocupante.");
        }

        if (new HashSet<>(ocupantesIds).size() != ocupantesIds.size()) {
            throw new IllegalArgumentException("Ocupantes duplicados.");
        }

        var reg = RegistroAcesso.builder()
                .tipoPessoa(req.tipoPessoa())
                .idPessoa(req.idPessoa())
                .codPortaria(req.codPortaria())
                .entrada(OffsetDateTime.now(ZONE_ID))
                .observacao(req.observacao())
                .build();

        reg = registroRepo.save(reg);

        for (UUID idOcupante : ocupantesIds) {
            ocupanteRepo.save(
                    RegistroAcessoOcupante.builder()
                            .registro(reg)
                            .idColaborador(idOcupante)
                            .build()
            );
        }

        return montarResponse(reg);
    }

    @Transactional
    public AcessoResponse criarPorMatricula(AcessoCreatePorMatriculaRequest req) {
        Objects.requireNonNull(req.tipoPessoa(), "tipoPessoa é obrigatório");
        Objects.requireNonNull(req.matriculaOuDocumento(), "matriculaOuDocumento é obrigatório");
        Objects.requireNonNull(req.codPortaria(), "codPortaria é obrigatório");
        if (!PORTARIAS_VALIDAS.contains(req.codPortaria())) {
            throw new IllegalArgumentException("Portaria inválida");
        }

        UUID idCondutor = switch (req.tipoPessoa()) {
            case COLABORADOR -> userRepo.findByMatricula(req.matriculaOuDocumento())
                    .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado pela matrícula"))
                    .getId();
            case VISITANTE -> visitanteRepo.findByNumeroDocumento(req.matriculaOuDocumento())
                    .orElseThrow(() -> new NoSuchElementException("Visitante não encontrado pelo documento"))
                    .getId();
        };

        List<UUID> ocupantesIds = List.of();

        if (req.tipoPessoa() == TipoPessoa.COLABORADOR) {
            if (req.ocupantesMatriculas() != null && !req.ocupantesMatriculas().isEmpty()) {
                var encontrados = userRepo.findByMatriculaIn(req.ocupantesMatriculas());
                Map<String, UUID> mapa = encontrados.stream()
                        .collect(Collectors.toMap(u -> u.getMatricula(), u -> u.getId()));

                ocupantesIds = req.ocupantesMatriculas().stream().map(m -> {
                    UUID id = mapa.get(m);
                    if (id == null) throw new NoSuchElementException("Matrícula não encontrada: " + m);
                    return id;
                }).toList();
            }

        } else {
            if (req.ocupantesDocumentos() != null && !req.ocupantesDocumentos().isEmpty()) {
                List<String> documentos = req.ocupantesDocumentos().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .toList();

                List<UUID> coletados = new ArrayList<>(documentos.size());
                for (String doc : documentos) {
                    var v = visitanteRepo.findByNumeroDocumento(doc)
                            .orElseThrow(() -> new NoSuchElementException("Documento de visitante não encontrado: " + doc));
                    coletados.add(v.getId());
                }
                ocupantesIds = List.copyOf(coletados);
            }
        }

        if (ocupantesIds.contains(idCondutor)) {
            throw new IllegalArgumentException("O condutor não pode ser ocupante.");
        }
        if (new HashSet<>(ocupantesIds).size() != ocupantesIds.size()) {
            throw new IllegalArgumentException("Ocupantes duplicados.");
        }

        return criar(new AcessoCreateRequest(
                req.tipoPessoa(), idCondutor, req.codPortaria(), req.observacao(), ocupantesIds
        ));
    }

    @Transactional
    public AcessoResponse registrarSaida(UUID idRegistro, AcessoSaidaRequest req) {
        var reg = registroRepo.findById(idRegistro)
                .orElseThrow(() -> new NoSuchElementException("Registro não encontrado"));
        if (reg.getSaida() != null) throw new IllegalStateException("Registro já está fechado.");

        reg.setSaida(OffsetDateTime.now(ZONE_ID));

        if (req != null && req.observacao() != null && !req.observacao().isBlank()) {
            reg.setObservacao(reg.getObservacao() == null
                    ? req.observacao()
                    : (reg.getObservacao() + " | " + req.observacao()));
        }
        reg = registroRepo.save(reg);
        return montarResponse(reg);
    }

    public List<AcessoResponse> listarAbertos() {
        return registroRepo.findAbertos().stream()
                .map(this::montarResponse)
                .toList();
    }

    public List<AcessoResponse> listarHistoricoPorData(LocalDate de, LocalDate ate) {
        LocalDate hoje = LocalDate.now();
        LocalDate ini = (de  != null) ? de  : hoje.minusDays(7);
        LocalDate fim = (ate != null) ? ate : hoje;
        if (fim.isBefore(ini)) {
            throw new IllegalArgumentException("Parâmetro 'ate' não pode ser anterior a 'de'.");
        }

        var inicioLocal = ini.atStartOfDay(ZONE_ID).toOffsetDateTime();
        var fimExclusivoLocal = fim.plusDays(1).atStartOfDay(ZONE_ID).toOffsetDateTime();

        var inicioUTC = inicioLocal.withOffsetSameInstant(ZoneOffset.UTC);
        var fimExclusivoUTC = fimExclusivoLocal.withOffsetSameInstant(ZoneOffset.UTC);

        return registroRepo
                .findByEntradaGreaterThanEqualAndEntradaLessThanOrderByEntradaDesc(inicioUTC, fimExclusivoUTC)
                .stream()
                .map(this::montarResponse)
                .toList();
    }

    public List<AcessoResponse> listarHistoricoSomentePortaria(Short codPortaria) {
        return registroRepo.findByCodPortariaOrderByEntradaDesc(codPortaria)
                .stream().map(this::montarResponse).toList();
    }

    public List<AcessoResponse> listarHistoricoSomenteTipo(TipoPessoa tipoPessoa) {
        return registroRepo.findByTipoPessoaOrderByEntradaDesc(tipoPessoa)
                .stream().map(this::montarResponse).toList();
    }

    private void validarPessoaExiste(TipoPessoa tipo, UUID id) {
        if (tipo == TipoPessoa.COLABORADOR) {
            userRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));
        } else {
            visitanteRepo.findByIdAndAtivoTrue(id)
                    .orElseThrow(() -> new NoSuchElementException("Visitante não encontrado/inativo"));
        }
    }

    private AcessoResponse montarResponse(RegistroAcesso r) {
        PessoaMinDTO condutor;
        if (r.getTipoPessoa() == TipoPessoa.COLABORADOR) {
            var resumo = userRepo.findResumoByIdColaborador(r.getIdPessoa()).orElse(null);
            condutor = new PessoaMinDTO(r.getIdPessoa(), resumo != null ? resumo.getNome() : null);
        } else {
            var v = visitanteRepo.findById(r.getIdPessoa()).orElse(null);
            condutor = new PessoaMinDTO(r.getIdPessoa(), v != null ? v.getNomeCompleto() : null);
        }

        var ocupantes = ocupanteRepo.findByRegistroId(r.getId()).stream()
                .map(oc -> {
                    var res = userRepo.findResumoByIdColaborador(oc.getIdColaborador()).orElse(null);
                    if (res != null) {
                        return new PessoaMinDTO(oc.getIdColaborador(), res.getNome());
                    }
                    var v = visitanteRepo.findById(oc.getIdColaborador()).orElse(null);
                    return new PessoaMinDTO(oc.getIdColaborador(), v != null ? v.getNomeCompleto() : null);
                })
                .toList();

        return new AcessoResponse(
                r.getId(),
                r.getTipoPessoa(),
                condutor,
                r.getCodPortaria(),
                r.getEntrada(),
                r.getSaida(),
                r.getObservacao(),
                ocupantes
        );
    }
}
