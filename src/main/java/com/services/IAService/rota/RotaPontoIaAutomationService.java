package com.services.IAService.rota;

import com.dto.IA.ponto.CriarPontoIAResult;

public interface RotaPontoIaAutomationService {

    CriarPontoIAResult interpretarComando(String comandoUsuario);
}
