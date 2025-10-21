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

        if (req.tipoPessoa() == TipoPessoa.VISITANTE && !ocupantesIds.isEmpty()) {
            throw new IllegalArgumentException("Visitante não pode ter ocupantes neste endpoint.");
        }
        if (ocupantesIds.stream().anyMatch(id -> id.equals(req.idPessoa()))) {
            throw new IllegalArgumentException("O condutor não pode ser ocupante.");
        }
        if (new HashSet<>(ocupantesIds).size() != ocupantesIds.size()) {
            throw new IllegalArgumentException("Ocupantes duplicados.");
        }
        if (!ocupantesIds.isEmpty()) {
            var encontrados = userRepo.findAllById(ocupantesIds);
            if (encontrados.size() != ocupantesIds.size()) {
                throw new IllegalArgumentException("Algum ocupante não existe/está inativo.");
            }
        }

        OffsetDateTime entradaAgora = OffsetDateTime.now(ZONE_ID);

        var reg = RegistroAcesso.builder()
                .tipoPessoa(req.tipoPessoa())
                .idPessoa(req.idPessoa())
                .codPortaria(req.codPortaria())
                .entrada(entradaAgora)
                .observacao(req.observacao())
                .build();

        reg = registroRepo.save(reg);

        for (UUID idColab : ocupantesIds) {
            ocupanteRepo.save(
                    RegistroAcessoOcupante.builder()
                            .registro(reg)
                            .idColaborador(idColab)
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
        if (req.tipoPessoa() == TipoPessoa.COLABORADOR
                && req.ocupantesMatriculas() != null && !req.ocupantesMatriculas().isEmpty()) {
            var encontrados = userRepo.findByMatriculaIn(req.ocupantesMatriculas());
            Map<String, UUID> mapa = encontrados.stream()
                    .collect(Collectors.toMap(u -> u.getMatricula(), u -> u.getId()));
            ocupantesIds = req.ocupantesMatriculas().stream().map(m -> {
                UUID id = mapa.get(m);
                if (id == null) throw new NoSuchElementException("Matrícula não encontrada: " + m);
                return id;
            }).toList();
        } else if (req.tipoPessoa() == TipoPessoa.VISITANTE && req.ocupantesMatriculas() != null) {
            throw new IllegalArgumentException("Visitante não pode ter ocupantes neste endpoint.");
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

        OffsetDateTime inicioLocal = ini.atStartOfDay(ZONE_ID).toOffsetDateTime();

        OffsetDateTime fimExclusivoLocal = fim.plusDays(1).atStartOfDay(ZONE_ID).toOffsetDateTime();

        OffsetDateTime inicioUTC = inicioLocal.withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime fimExclusivoUTC = fimExclusivoLocal.withOffsetSameInstant(ZoneOffset.UTC);

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
                    return new PessoaMinDTO(oc.getIdColaborador(), res != null ? res.getNome() : null);
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