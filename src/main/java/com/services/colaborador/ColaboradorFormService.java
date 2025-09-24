package com.services.colaborador;

import com.domain.user.Enum.StatusForm;
import com.dto.colaborador.ColaboradorDTO;
import com.dto.colaborador.FormCreateRequest;
import com.dto.colaborador.FormResponse;

import java.util.List;
import java.util.UUID;

public interface ColaboradorFormService{
    FormResponse criarPara(ColaboradorDTO colab, FormCreateRequest req);
    List<FormResponse> listarTodosDoColaborador(UUID idColaborador, StatusForm status);
    List<FormResponse> listarTodos(StatusForm status);
    FormResponse atualizarStatus(UUID idForm, StatusForm novoStatus, UUID idUsuarioAcionador);

}
