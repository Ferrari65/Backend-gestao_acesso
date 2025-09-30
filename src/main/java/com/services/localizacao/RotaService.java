package com.services.localizacao;

import com.domain.user.Rotas.Rota;
import com.dto.PATCH.RotaPatchDTO;
import com.dto.localizacao.Rota.RotaRequestDTO;

import java.util.List;

public interface RotaService {
    List<Rota> listar();
    Rota buscar (Integer id);
    Rota criar(RotaRequestDTO dto);
    Rota atualizar(Integer idRota, RotaRequestDTO dto);
    Rota patch(Integer idRota, RotaPatchDTO dto);
    void deletar(Integer idRota);
}
