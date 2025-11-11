package com.services.IAService.pontos;

import com.dto.localizacao.Ponto.DadosCriarPontoIa;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IaInterpretadorPontoService {

    private static final Logger log = LoggerFactory.getLogger(IaInterpretadorPontoService.class);

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public DadosCriarPontoIa extrairDados(String textoUsuario) {
        String systemPrompt = """
            Você é a IA oficial do sistema TrackPass Gestão de Acesso.

            Sua função é APENAS extrair os dados necessários para criar um ponto de rota
            a partir da frase do usuário.

            A partir da mensagem do usuário, você deve identificar:
            - nome do ponto
            - endereço completo (rua, número, cidade, estado e país)
            - id da cidade (se a cidade não for indicada, use idCidade = 1 como padrão)

            O campo "endereco" DEVE SEMPRE estar no formato:
            "Rua/Nome da via, número, cidade - UF, Brasil"

            Exemplo:
            {
              "nomePonto": "Ponto da escola",
              "endereco": "Rua São José, 250, São Joaquim da Barra - SP, Brasil",
              "idCidade": 1
            }

            Regras IMPORTANTES:
            - Não escreva nenhuma explicação antes ou depois do JSON.
            - Não use markdown, não use ```json ou ``` de nenhum tipo.
            - Não coloque comentários dentro do JSON.
            - Não adicione campos extras.
            - Se o usuário mencionar rota, ignore esse dado e foque apenas no ponto.
            """;

        String prompt = systemPrompt + "\n\nMensagem do usuário: " + textoUsuario;

        String respostaBruta = chatModel.call(prompt);
        log.info("Resposta bruta da IA para criação de ponto: {}", respostaBruta);

        String semMarkdown = respostaBruta
                .replace("```json", "")
                .replace("```", "")
                .trim();

        String jsonLimpo = semMarkdown.replaceAll("(?s).*?(\\{.*\\}).*", "$1");

        try {
            return objectMapper.readValue(jsonLimpo, DadosCriarPontoIa.class);
        } catch (Exception e) {
            log.error("Falha ao interpretar resposta da IA: {}", respostaBruta, e);
            throw new IllegalStateException("Falha ao interpretar resposta da IA. Resposta recebida: " + respostaBruta, e);
        }
    }
}

