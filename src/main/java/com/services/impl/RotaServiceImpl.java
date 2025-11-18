package com.services.impl;

import com.domain.user.Enum.Periodo;
import com.domain.user.Rotas.Rota;
import com.domain.user.Rotas.RotaPonto;
import com.domain.user.endereco.Pontos;
import com.dto.IA.ponto.CriarPontoIAResult;
import com.dto.IA.rota.RotaComMaisEmbarquesHojeDTO;
import com.dto.IA.rota.RotaIARequestDTO;
import com.dto.PATCH.RotaPatchDTO;
import com.dto.localizacao.Rota.*;
import com.projection.RotaComMaisColaboradoresProjection;
import com.projection.RotaComMaisEmbarquesHojeProjection;
import com.repositories.Rota.RotaColaboradorRepository;
import com.repositories.Rota.RotaPontoRepository;
import com.repositories.Rota.RotaRepository;
import com.repositories.localizacao.CidadeRepository;
import com.repositories.localizacao.PontosRepository;
import com.repositories.registroEmbarque.RegistroEmbarqueRepository;
import com.services.localizacao.RotaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RotaServiceImpl implements RotaService {

    private final RotaRepository rotaRepo;
    private final CidadeRepository cidadeRepo;
    private final PontosRepository pontoRepo;
    private final RotaPontoRepository rotaPontoRepo;
    private final RotaColaboradorRepository rotaColabRepo;
    private final RegistroEmbarqueRepository regEmbarqueRepo;

    private static final DateTimeFormatter FMT_HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private LocalTime toLocalTime(String valor) {
        return LocalTime.parse(valor, FMT_HH_MM);
    }

    @Transactional
    public Rota criarBasico(RotaIARequestDTO dto) {

        Integer idCidade = dto.idCidade();
        if (idCidade == null && dto.cidadeNome() != null && !dto.cidadeNome().isBlank()) {
            var cidadeEncontrada = cidadeRepo
                    .findByNomeIgnoreCase(dto.cidadeNome().trim())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Cidade não encontrada com nome: " + dto.cidadeNome()
                    ));
            idCidade = cidadeEncontrada.getIdCidade();
        }

        var cidade = cidadeRepo.findById(idCidade)
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        var nome = dto.nome().trim().toUpperCase();

        if (rotaRepo.existsByCidade_IdCidadeAndNomeIgnoreCaseAndPeriodo(
                idCidade, nome, dto.periodo())) {
            throw conflito("Já existe uma rota com este nome e período nesta cidade");
        }

        Rota rota = new Rota();
        rota.setCidade(cidade);
        rota.setNome(nome);
        rota.setPeriodo(dto.periodo());
        rota.setCapacidade(dto.capacidade());
        rota.setAtivo(Boolean.TRUE.equals(dto.ativo()));

        if (dto.horaPartida() != null && !dto.horaPartida().isBlank()) {
            rota.setHoraPartida(toLocalTime(dto.horaPartida()));
        }
        if (dto.horaChegada() != null && !dto.horaChegada().isBlank()) {
            rota.setHoraChegada(toLocalTime(dto.horaChegada()));
        }

        return rotaRepo.save(rota);
    }

    @Override
    public List<Rota> listar() {
        return rotaRepo.findAll();
    }

    public long contarRotasAtivas() {
        return rotaRepo.countByAtivoTrue();
    }

    public long contarRotasInativas() {
        return rotaRepo.countByAtivoFalse();
    }

    @Override
    public Rota buscar(Integer id) {
        return rotaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));
    }

    @Override
    @Transactional
    public Rota criar(RotaRequestDTO dto) {
        var cidade = cidadeRepo.findById(dto.idCidade())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        validarOrdemUnica(dto.pontos());

        var nome = dto.nome().trim().toUpperCase();
        if (rotaRepo.existsByCidade_IdCidadeAndNomeIgnoreCaseAndPeriodo(
                dto.idCidade(), nome, dto.periodo())) {
            throw conflito("Já existe uma rota com este nome e período nesta cidade");
        }

        var mapaPontos = carregarEValidarPontos(dto.idCidade(), dto.pontos());

        var rota = new Rota();
        rota.setCidade(cidade);
        rota.setNome(nome);
        rota.setPeriodo(dto.periodo());
        rota.setCapacidade(dto.capacidade());
        rota.setAtivo(Boolean.TRUE.equals(dto.ativo()));
        rota.setHoraPartida(dto.horaPartida());
        rota.setHoraChegada(dto.horaChegada());

        aplicarSequenciaInPlace(rota, dto.pontos(), mapaPontos);

        return rotaRepo.save(rota);
    }

    @Override
    @Transactional
    public Rota atualizar(Integer idRota, RotaRequestDTO dto) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));

        validarOrdemUnica(dto.pontos());

        var cidade = cidadeRepo.findById(dto.idCidade())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        var nome = dto.nome().trim().toUpperCase();
        if (rotaRepo.existsByCidade_IdCidadeAndNomeIgnoreCaseAndPeriodoAndIdRotaNot(
                dto.idCidade(), nome, dto.periodo(), idRota)) {
            throw conflito("Já existe uma rota com este nome e período nesta cidade");
        }

        var mapaPontos = carregarEValidarPontos(dto.idCidade(), dto.pontos());

        rota.setCidade(cidade);
        rota.setNome(nome);
        rota.setPeriodo(dto.periodo());
        rota.setCapacidade(dto.capacidade());
        rota.setAtivo(dto.ativo());
        rota.setHoraPartida(dto.horaPartida());
        rota.setHoraChegada(dto.horaChegada());

        aplicarSequenciaInPlace(rota, dto.pontos(), mapaPontos);

        return rotaRepo.save(rota);
    }

    public RotaComMaisColaboradoresDTO buscarRotaComMaisColaboradores() {
        RotaComMaisColaboradoresProjection proj =
                rotaColabRepo.findRotaComMaisColaboradores();

        if (proj == null) {
            throw new EntityNotFoundException(
                    "Nenhuma rota possui colaboradores vinculados."
            );
        }

        return new RotaComMaisColaboradoresDTO(
                proj.getNomeRota(),
                proj.getQuantidadeColaboradores()
        );
    }

    public String montarMensagemRotaComMaisColaboradores() {
        RotaComMaisColaboradoresDTO dto = buscarRotaComMaisColaboradores();

        return "A rota com mais colaboradores é **" + dto.nomeRota()
                + "**, com " + dto.quantidadeColaboradores()
                + " colaboradores cadastrados.";
    }

    public long buscarTotalColaboradoresPorNomeRota(String nomeRota) {
        String nomeNormalizado = nomeRota.trim();
        return rotaColabRepo.contarColaboradoresPorNomeRota(nomeNormalizado);
    }

    public String montarMensagemTotalColaboradoresPorRota(String nomeRota) {
        long total = buscarTotalColaboradoresPorNomeRota(nomeRota);

        if (total == 0) {
            return "A " + nomeRota + " não possui colaboradores vinculados no momento.";
        }

        return "A " + nomeRota + " possui " + total + " colaboradores vinculados.";
    }

    public long buscarTotalColaboradoresPorNomeRotaEPeriodo(String nomeRota, Periodo periodo) {
        String nomeNormalizado = nomeRota.trim();
        return rotaColabRepo.contarColaboradoresPorNomeRotaEPeriodo(
                nomeNormalizado,
                periodo.name()
        );
    }

    public String montarMensagemTotalColaboradoresPorRotaEPeriodo(String nomeRota, Periodo periodo) {
        long total = buscarTotalColaboradoresPorNomeRotaEPeriodo(nomeRota, periodo);
        String periodoTexto = formatarPeriodo(periodo);

        if (total == 0) {
            return "A " + nomeRota + " no período da " + periodoTexto
                    + " não possui colaboradores vinculados no momento.";
        }

        return "A " + nomeRota + " no período da " + periodoTexto
                + " possui " + total + " colaboradores vinculados.";
    }

    private String formatarPeriodo(Periodo periodo) {
        return switch (periodo) {
            case MANHA -> "manhã";
            case TARDE -> "tarde";
            case NOITE -> "noite";
            default -> periodo.name().toLowerCase();
        };
    }

    public RotaComMaisEmbarquesHojeDTO buscarRotaComMaisEmbarquesHoje() {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        RotaComMaisEmbarquesHojeProjection proj =
                regEmbarqueRepo.findRotaComMaisEmbarquesNaData(hoje);

        if (proj == null) {
            throw new EntityNotFoundException(
                    "Nenhuma rota possui embarques registrados hoje."
            );
        }

        return new RotaComMaisEmbarquesHojeDTO(
                proj.getNomeRota(),
                proj.getTotalEmbarques()
        );
    }

    public String montarMensagemRotaComMaisEmbarquesHoje() {
        try {
            RotaComMaisEmbarquesHojeDTO dto = buscarRotaComMaisEmbarquesHoje();

            return "Hoje, a rota com mais embarques é "
                    + dto.nomeRota()
                    + ", com "
                    + dto.totalEmbarques()
                    + " embarques registrados.";
        } catch (EntityNotFoundException e) {
            return "Hoje ainda não há nenhum embarque registrado em nenhuma rota.";
        }
    }

    @Override
    @Transactional
    public Rota patch(Integer idRota, RotaPatchDTO dto) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));

        if (dto.idCidade() != null) {
            var cidade = cidadeRepo.findById(dto.idCidade())
                    .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));
            rota.setCidade(cidade);
        }
        if (dto.nome() != null) rota.setNome(dto.nome().trim().toUpperCase());
        if (dto.periodo() != null) rota.setPeriodo(dto.periodo());
        if (dto.capacidade() != null) {
            if (dto.capacidade() < 0) throw new IllegalArgumentException("Capacidade não pode ser negativa");
            rota.setCapacidade(dto.capacidade());
        }
        if (dto.ativo() != null) rota.setAtivo(dto.ativo());

        if (dto.horaPartida() != null) rota.setHoraPartida(toLocalTime(dto.horaPartida()));
        if (dto.horaChegada() != null) rota.setHoraChegada(toLocalTime(dto.horaChegada()));

        if (dto.pontos() != null) {
            validarOrdemUnica(dto.pontos());
            var idCidade = rota.getCidade().getIdCidade();
            var mapaPontos = carregarEValidarPontos(idCidade, dto.pontos());
            aplicarSequenciaInPlace(rota, dto.pontos(), mapaPontos);
        }

        boolean conflito = rotaRepo.existsByCidade_IdCidadeAndNomeIgnoreCaseAndPeriodoAndIdRotaNot(
                rota.getCidade().getIdCidade(), rota.getNome(), rota.getPeriodo(), rota.getIdRota());
        if (conflito) throw conflito("Já existe uma rota com este nome e período nesta cidade");

        return rotaRepo.save(rota);
    }

    @Override
    @Transactional
    public void deletar(Integer idRota) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));
        rotaRepo.delete(rota);
    }

    @Transactional(readOnly = true)
    public List<RotaPontoItemDTO> listarTrajeto(Integer idRota) {
        rotaRepo.findById(idRota).orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));
        return rotaPontoRepo.listarPontosDTO(idRota);
    }

    @Transactional
    public Rota atribuirPontoPorNomes(CriarPontoIAResult cmd) {

        if (cmd.nomeRota() == null || cmd.nomeRota().isBlank()) {
            throw new IllegalArgumentException("Nome da rota é obrigatório");
        }
        if (cmd.nomePonto() == null || cmd.nomePonto().isBlank()) {
            throw new IllegalArgumentException("Nome do ponto é obrigatório");
        }
        if (cmd.ordem() == null || cmd.ordem() <= 0) {
            throw new IllegalArgumentException("A ordem deve ser um número inteiro > 0");
        }

        String nomeRotaNormalizado = cmd.nomeRota().trim().toUpperCase();

        Rota rota = rotaRepo.findByNomeIgnoreCase(nomeRotaNormalizado)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rota não encontrada com nome: " + cmd.nomeRota()
                ));

        Integer idCidade = rota.getCidade().getIdCidade();
        Pontos ponto = pontoRepo
                .findByNomeIgnoreCaseAndCidade_IdCidade(cmd.nomePonto().trim(), idCidade)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ponto não encontrado com nome: " + cmd.nomePonto()
                ));
        List<RotaPontoItemRequestDTO> itens = rota.getPontos().stream()
                .map(rp -> new RotaPontoItemRequestDTO(
                        rp.getPonto().getIdPonto(),
                        rp.getOrdem()
                ))
                .collect(Collectors.toList());

        boolean jaExistia = itens.stream()
                .anyMatch(it -> it.idPonto().equals(ponto.getIdPonto()));

        if (jaExistia) {
            itens = itens.stream()
                    .map(it -> it.idPonto().equals(ponto.getIdPonto())
                            ? new RotaPontoItemRequestDTO(ponto.getIdPonto(), cmd.ordem())
                            : it
                    )
                    .collect(Collectors.toList());
        } else {
            itens.add(new RotaPontoItemRequestDTO(ponto.getIdPonto(), cmd.ordem()));
        }
        validarOrdemUnica(itens);
        var mapaPontos = carregarEValidarPontos(idCidade, itens);
        aplicarSequenciaInPlace(rota, itens, mapaPontos);
        return rotaRepo.save(rota);
    }

    private void validarOrdemUnica(List<RotaPontoItemRequestDTO> itens) {
        var set = new HashSet<Integer>();
        for (var it : itens) {
            if (it.ordem() == null || it.ordem() <= 0) {
                throw new IllegalArgumentException("A ordem de cada ponto deve ser > 0");
            }
            if (!set.add(it.ordem())) {
                throw new IllegalArgumentException("Há ordens repetidas na sequência de pontos");
            }
        }
    }

    private Map<Integer, Pontos> carregarEValidarPontos(Integer idCidade, List<RotaPontoItemRequestDTO> itens) {
        var ids = itens.stream().map(RotaPontoItemRequestDTO::idPonto).toList();
        var pontos = pontoRepo.findAllById(ids);

        if (pontos.size() != new HashSet<>(ids).size())
            throw new EntityNotFoundException("Um ou mais pontos não foram encontrados");

        boolean ok = pontos.stream().allMatch(p ->
                p.getCidade() != null && Objects.equals(p.getCidade().getIdCidade(), idCidade));
        if (!ok) throw new IllegalArgumentException("Todos os pontos devem pertencer à mesma cidade da rota");

        return pontos.stream().collect(Collectors.toMap(Pontos::getIdPonto, p -> p));
    }

    private void aplicarSequenciaInPlace(Rota rota, List<RotaPontoItemRequestDTO> itens, Map<Integer, Pontos> mapaPontos) {
        Map<Integer, RotaPonto> existentes = rota.getPontos().stream()
                .collect(Collectors.toMap(rp -> rp.getPonto().getIdPonto(), rp -> rp));

        var itensOrdenados = itens.stream()
                .sorted(Comparator.comparingInt(RotaPontoItemRequestDTO::ordem))
                .toList();

        Set<Integer> manter = new HashSet<>();

        for (var it : itensOrdenados) {
            Integer idPonto = it.idPonto();
            Pontos ponto = mapaPontos.get(idPonto);

            RotaPonto rp = existentes.get(idPonto);
            if (rp == null) {
                rp = new RotaPonto();
                rp.setRota(rota);
                rp.setPonto(ponto);
                rp.setOrdem(it.ordem());
                rota.getPontos().add(rp);
            } else {
                rp.setOrdem(it.ordem());
            }
            manter.add(idPonto);
        }

        rota.getPontos().removeIf(rp -> !manter.contains(rp.getPonto().getIdPonto()));
    }

    private RuntimeException conflito(String msg) {
        return new DataIntegrityViolationException(msg);
    }
}
