package com.services.localizacao;

import com.domain.user.Rotas.Rota;
import com.domain.user.Rotas.RotaPonto;
import com.domain.user.Rotas.RotaPontoId;
import com.domain.user.endereco.Pontos;
import com.dto.PATCH.RotaPatchDTO;
import com.dto.localizacao.Rota.RotaPontoItemRequestDTO;
import com.dto.localizacao.Rota.RotaRequestDTO;
import com.repositories.Rota.RotaRepository;
import com.repositories.localizacao.CidadeRepository;
import com.repositories.localizacao.PontosRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RotaService {

    private final RotaRepository rotaRepo;
    private final CidadeRepository cidadeRepo;
    private final PontosRepository pontoRepo;

    private static final DateTimeFormatter HHMMSS = DateTimeFormatter.ofPattern("HH:mm:ss");

    public List<Rota> listar() {
        return rotaRepo.findAll();
    }

    public Rota buscar(Integer id) {
        return rotaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));
    }

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

        rota.getPontos().clear();
        rota.getPontos().addAll(montarSequencia(rota, dto.pontos(), mapaPontos));

        return rotaRepo.save(rota);
    }

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

        rota.getPontos().clear();
        rota.getPontos().addAll(montarSequencia(rota, dto.pontos(), mapaPontos));

        return rotaRepo.save(rota);
    }

    @Transactional
    public Rota patch(Integer idRota, RotaPatchDTO dto) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));

        boolean mudouCidade = false, mudouNome = false, mudouPeriodo = false;

        if (dto.idCidade() != null) {
            var cidade = cidadeRepo.findById(dto.idCidade())
                    .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));
            rota.setCidade(cidade);
            mudouCidade = true;
        }
        if (dto.nome() != null) {
            rota.setNome(dto.nome().trim().toUpperCase());
            mudouNome = true;
        }
        if (dto.periodo() != null) {
            rota.setPeriodo(dto.periodo());
            mudouPeriodo = true;
        }
        if (dto.capacidade() != null) {
            if (dto.capacidade() < 0) throw new IllegalArgumentException("Capacidade não pode ser negativa");
            rota.setCapacidade(dto.capacidade());
        }
        if (dto.ativo() != null) rota.setAtivo(dto.ativo());

        if (dto.horaPartida() != null && !"string".equalsIgnoreCase(dto.horaPartida())) {
            rota.setHoraPartida(LocalTime.parse(dto.horaPartida(), HHMMSS));
        }
        if (dto.horaChegada() != null && !"string".equalsIgnoreCase(dto.horaChegada())) {
            rota.setHoraChegada(LocalTime.parse(dto.horaChegada(), HHMMSS));
        }

        if (dto.pontos() != null) {
            validarOrdemUnica(dto.pontos());
            var mapaPontos = carregarEValidarPontos(rota.getCidade().getIdCidade(), dto.pontos());
            rota.getPontos().clear();
            rota.getPontos().addAll(montarSequencia(rota, dto.pontos(), mapaPontos));
        }

        if (mudouNome || mudouPeriodo || mudouCidade) {
            boolean conflito = rotaRepo.existsByCidade_IdCidadeAndNomeIgnoreCaseAndPeriodoAndIdRotaNot(
                    rota.getCidade().getIdCidade(), rota.getNome(), rota.getPeriodo(), rota.getIdRota());
            if (conflito) throw conflito("Já existe uma rota com este nome e período nesta cidade");
        }

        return rotaRepo.save(rota);
    }

    @Transactional
    public void deletar(Integer idRota) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new EntityNotFoundException("Rota não encontrada"));
        rotaRepo.delete(rota);
    }

    private void validarOrdemUnica(List<RotaPontoItemRequestDTO> itens) {
        var set = new HashSet<Integer>();
        for (var it : itens) {
            if (it.ordem() == null || it.ordem() <= 0)
                throw new IllegalArgumentException("A ordem de cada ponto deve ser > 0");
            if (!set.add(it.ordem()))
                throw new IllegalArgumentException("Há ordens repetidas na sequência de pontos");
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

    private List<RotaPonto> montarSequencia(Rota rota, List<RotaPontoItemRequestDTO> itens, Map<Integer, Pontos> mapaPontos) {
        return itens.stream()
                .sorted(Comparator.comparingInt(RotaPontoItemRequestDTO::ordem))
                .map(it -> {
                    var rp = new RotaPonto();
                    rp.setId(new RotaPontoId());
                    rp.setRota(rota);
                    rp.setPonto(mapaPontos.get(it.idPonto()));
                    rp.setOrdem(it.ordem());
                    return rp;
                })
                .toList();
    }

    private RuntimeException conflito(String msg) {
        return new DataIntegrityViolationException(msg);
    }
}
