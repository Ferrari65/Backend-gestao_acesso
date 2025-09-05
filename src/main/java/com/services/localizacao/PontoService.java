package com.services.localizacao;

import com.domain.user.endereco.Pontos;
import com.dto.localizacao.PontosRequestDTO;
import com.repositories.localizacao.CidadeRepository;
import com.repositories.localizacao.PontosRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PontoService {
    private final PontosRepository repository;
    private final CidadeRepository cidadeRepository;

    public PontoService(PontosRepository repository, CidadeRepository cidadeRepository) {
        this.repository = repository;
        this.cidadeRepository = cidadeRepository;
    }

    public Pontos criar(PontosRequestDTO dto){
        var cidade = cidadeRepository.findById(dto.idCidade())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        var p = new Pontos();
        p.setCidade(cidade);
        p.setNome(dto.nome());
        p.setEndereco(dto.endereco());
        p.setLatitude(dto.latitude());
        p.setLongitude(dto.longitude());

        return repository.save(p);
    }
}