package com.services.IAService.pontos;

import com.domain.user.endereco.Pontos;

public interface PontoIaAutomationService {
    Pontos criarPontoAPartirDeTexto(String comandoUsuario);
}
