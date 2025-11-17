package com.services.impl;

import com.dto.IA.ponto.CriarPontoIAResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.IAService.rota.RotaPontoIaAutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RotaPontoIaAutomationServiceImpl implements RotaPontoIaAutomationService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    @Override
    public CriarPontoIAResult interpretarComando(String comandoUsuario) {

        String systemPrompt = """
        Você é um assistente que extrai dados para atribuir um ponto a uma rota.

        Dado um comando em português, você deve retornar SOMENTE um JSON no formato:

        {
          "nomeRota": "string",
          "nomePonto": "string",
          "ordem": numero_inteiro
        }

        - "nomeRota": nome da rota mencionada pelo usuário.
        - "nomePonto": nome do ponto mencionado pelo usuário.
        - "ordem": posição do ponto na rota, como número inteiro (1,2,3,...).

        Não escreva explicações, só o JSON.
        """;

        String json = chatModel.call(systemPrompt + "\nUsuário: " + comandoUsuario);

        try {
            return objectMapper.readValue(json, CriarPontoIAResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao interpretar o comando da IA. Resposta: " + json, e);
        }
    }
}