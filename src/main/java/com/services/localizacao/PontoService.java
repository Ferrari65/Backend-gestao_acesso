package com.services.localizacao;

import com.domain.user.endereco.Pontos;
import com.dto.localizacao.Ponto.PontosRequestDTO;

import java.util.List;

public interface PontoService {

    List<Pontos> listar();
    Pontos buscar (Integer id);
    Pontos criar (PontosRequestDTO dto);
    Pontos atualizar (Integer id, PontosRequestDTO dto);
    void excluir (Integer id);
}
