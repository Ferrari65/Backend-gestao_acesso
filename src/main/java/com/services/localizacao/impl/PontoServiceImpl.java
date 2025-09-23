package com.services.localizacao.impl;

import com.domain.user.endereco.Pontos;
import com.domain.user.endereco.Cidade;
import com.dto.localizacao.Ponto.PontosRequestDTO;
import com.repositories.localizacao.CidadeRepository;
import com.repositories.localizacao.PontosRepository;
import com.services.localizacao.PontoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PontoServiceImpl implements PontoService {

    private final PontosRepository repository;
    private final CidadeRepository cidadeRepository;

    @Override
    public List<Pontos> listar() {
        return repository.findAll();
    }

    @Override
    public Pontos buscar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ponto não encontrado"));
    }

    @Override
    public Pontos criar(PontosRequestDTO dto) {
        var cidade = cidadeRepository.findById(dto.idCidade())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        var nome = dto.nome().trim();
        var p = new Pontos();
        p.setCidade(cidade);
        p.setNome(nome);
        p.setEndereco(dto.endereco());
        p.setLatitude(dto.latitude());
        p.setLongitude(dto.longitude());

        return repository.save(p);
    }

    @Override
    @Transactional
    public Pontos atualizar(Integer id, PontosRequestDTO dto) {
        var p = buscar(id);

        if (!p.getCidade().getIdCidade().equals(dto.idCidade())) {
            Cidade cidade = cidadeRepository.findById(dto.idCidade())
                    .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));
            p.setCidade(cidade);
        }

        var nome = dto.nome().trim();
        p.setNome(nome);
        p.setEndereco(dto.endereco());
        p.setLatitude(dto.latitude());
        p.setLongitude(dto.longitude());

        return repository.save(p);
    }

    @Override
    @Transactional
    public void excluir (Integer id){
        if (!repository.existsById(id)){
            throw  new EntityNotFoundException("Ponto não encontrado");
        }
        repository.deleteById(id);
    }
}
