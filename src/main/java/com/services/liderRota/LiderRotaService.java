package com.services.liderRota;

import com.dto.liderRota.LiderRotaResponse;

import java.util.List;
import java.util.UUID;

public interface LiderRotaService {
    LiderRotaResponse atribuir(Integer idRota, UUID idColaborador);
    List<LiderRotaResponse> buscarTodos(Integer idRota);
    void remover(Integer idRota, UUID idColaborador);
}
