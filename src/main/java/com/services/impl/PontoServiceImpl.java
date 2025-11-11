package com.services.impl;

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
    @Transactional
    public Pontos criar(PontosRequestDTO dto) {
        Cidade cidade = cidadeRepository.findById(dto.idCidade())
                .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));

        String nome = dto.nome() != null ? dto.nome().trim() : null;
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do ponto é obrigatório.");
        }

        String endereco = dto.endereco() != null ? dto.endereco().trim() : null;
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("Endereço é obrigatório.");
        }

        String lowerEndereco = endereco.toLowerCase();

        if (endereco.contains(",,") || endereco.contains(", ,")) {
            throw new IllegalArgumentException("Endereço inválido. Está incompleto (contém vírgulas sem informação).");
        }

        if (lowerEndereco.matches(".*\\b0\\b.*")) {
            throw new IllegalArgumentException("Endereço inválido. Número '0' não é aceito.");
        }
        if (!lowerEndereco.matches(".*\\d+.*")) {
            throw new IllegalArgumentException("Endereço inválido. Informe também o número (ex: '250').");
        }

        if (!lowerEndereco.contains(cidade.getNome().toLowerCase())) {
            throw new IllegalArgumentException(
                    "Endereço inválido. O endereço deve conter o nome da cidade \"" + cidade.getNome() + "\"."
            );
        }

        Pontos p = new Pontos();
        p.setCidade(cidade);
        p.setNome(nome);
        p.setEndereco(endereco);
        p.setLatitude(dto.latitude());
        p.setLongitude(dto.longitude());

        return repository.save(p);
    }

    @Override
    @Transactional
    public Pontos atualizar(Integer id, PontosRequestDTO dto) {
        Pontos p = buscar(id);

        if (!p.getCidade().getIdCidade().equals(dto.idCidade())) {
            Cidade cidade = cidadeRepository.findById(dto.idCidade())
                    .orElseThrow(() -> new EntityNotFoundException("Cidade não encontrada"));
            p.setCidade(cidade);
        }

        String nome = dto.nome() != null ? dto.nome().trim() : null;
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do ponto é obrigatório.");
        }

        String endereco = dto.endereco() != null ? dto.endereco().trim() : null;
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("Endereço é obrigatório.");
        }

        String lowerEndereco = endereco.toLowerCase();

        if (endereco.contains(",,") || endereco.contains(", ,")) {
            throw new IllegalArgumentException("Endereço inválido. Está incompleto (contém vírgulas sem informação).");
        }

        if (lowerEndereco.matches(".*\\b0\\b.*")) {
            throw new IllegalArgumentException("Endereço inválido. Número '0' não é aceito.");
        }

        if (!lowerEndereco.matches(".*\\d+.*")) {
            throw new IllegalArgumentException("Endereço inválido. Informe também o número (ex: '250').");
        }

        if (!lowerEndereco.contains(p.getCidade().getNome().toLowerCase())) {
            throw new IllegalArgumentException(
                    "Endereço inválido. O endereço deve conter o nome da cidade \"" + p.getCidade().getNome() + "\"."
            );
        }

        p.setNome(nome);
        p.setEndereco(endereco);
        p.setLatitude(dto.latitude());
        p.setLongitude(dto.longitude());

        return repository.save(p);
    }

    @Override
    @Transactional
    public void excluir (Integer id){
        if (!repository.existsById(id)){
            throw new EntityNotFoundException("Ponto não encontrado");
        }
        repository.deleteById(id);
    }
}
