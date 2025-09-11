package com.services.localizacao;

import com.domain.user.Rotas.Rota;
import com.domain.user.Rotas.RotaPonto;
import com.domain.user.Rotas.RotaPontoId;
import com.domain.user.endereco.Pontos;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RotaService {

    private final RotaRepository rotaRepo;
    private final CidadeRepository cidadeRepo;
    private final PontosRepository pontoRepo;

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

        var nome = dto.nome().trim();
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
        rota.setCidade(cidade);

        rota.setNome(dto.nome().trim().toUpperCase());
        rota.setPeriodo(dto.periodo());
        rota.setCapacidade(dto.capacidade());
        rota.setAtivo(dto.ativo());
        rota.setHoraPartida(dto.horaPartida());
        rota.setHoraChegada(dto.horaChegada());

        var idsSolicitados = dto.pontos().stream().map(p -> p.idPonto()).toList();
        var pontos = pontoRepo.findAllById(idsSolicitados);
        if (pontos.size() != new java.util.HashSet<>(idsSolicitados).size()) {
            throw new EntityNotFoundException("Um ou mais pontos não foram encontrados");
        }
        boolean mesmaCidade = pontos.stream().allMatch(p ->
                p.getCidade() != null && p.getCidade().getIdCidade().equals(dto.idCidade()));
        if (!mesmaCidade) throw new IllegalArgumentException("Todos os pontos devem ser da mesma cidade da rota");

        var mapaPontos = pontos.stream()
                .collect(java.util.stream.Collectors.toMap(Pontos::getIdPonto, p -> p));

        var atuaisPorPonto = rota.getPontos().stream()
                .collect(java.util.stream.Collectors.toMap(rp -> rp.getPonto().getIdPonto(), rp -> rp));

        java.util.List<RotaPonto> novaSequencia = new java.util.ArrayList<>();

        dto.pontos().stream()
                .sorted(java.util.Comparator.comparingInt(p -> p.ordem()))
                .forEach(item -> {
                    var ponto = mapaPontos.get(item.idPonto());
                    RotaPonto rp = atuaisPorPonto.remove(item.idPonto());

                    if (rp == null) {
                        rp = new RotaPonto();
                        rp.setRota(rota);
                        rp.setPonto(ponto);
                    }
                    rp.setOrdem(item.ordem());
                    novaSequencia.add(rp);
                });
        rota.getPontos().removeAll(atuaisPorPonto.values());
        rota.getPontos().clear();
        rota.getPontos().addAll(novaSequencia);

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
            if (it.ordem() == null || it.ordem() <= 0) {
                throw new IllegalArgumentException("A ordem de cada ponto deve ser > 0");
            }
            if (!set.add(it.ordem())) {
                throw new IllegalArgumentException("Há ordens repetidas na sequência de pontos");
            }
        }
    }

    private Map<Integer, Pontos> carregarEValidarPontos(
            Integer idCidade, List<RotaPontoItemRequestDTO> itens) {

        var ids = itens.stream().map(RotaPontoItemRequestDTO::idPonto).toList();
        var pontos = pontoRepo.findAllById(ids);

        if (pontos.size() != new HashSet<>(ids).size()) {
            throw new EntityNotFoundException("Um ou mais pontos não foram encontrados");
        }

        boolean mesmaCidade = pontos.stream().allMatch(p ->
                p.getCidade() != null && Objects.equals(p.getCidade().getIdCidade(), idCidade));
        if (!mesmaCidade) {
            throw new IllegalArgumentException("Todos os pontos devem pertencer à mesma cidade da rota");
        }

        return pontos.stream()
                .collect(Collectors.toMap(Pontos::getIdPonto, p -> p));
    }

    private List<RotaPonto> montarSequencia(
            Rota rota,
            List<RotaPontoItemRequestDTO> itens,
            Map<Integer, Pontos> mapaPontos) {

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
