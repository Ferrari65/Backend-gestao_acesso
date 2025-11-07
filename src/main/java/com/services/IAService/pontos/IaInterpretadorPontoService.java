package com.services.IAService.pontos;

import com.dto.localizacao.Ponto.DadosCriarPontoIa;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IaInterpretadorPontoService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public DadosCriarPontoIa extrairDados(String textoUsuario) {
        String systemPrompt = """
            Você é a IA oficial do sistema TrackPass Gestão de Acesso.

            Sua função é APENAS extrair os dados necessários para criar um ponto de rota
            a partir da frase do usuário.

            A partir da mensagem do usuário, você deve identificar:
            - nome do ponto
            - endereço completo
            - id da cidade (se a cidade não for indicada, use idCidade = 1 como padrão)

            Responda SEMPRE apenas com um JSON válido, exatamente no formato:

            {
              "nomePonto": "nome do ponto",
              "endereco": "endereço completo",
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

        String semMarkdown = respostaBruta
                .replace("```json", "")
                .replace("```", "")
                .trim();

        String jsonLimpo = semMarkdown.replaceAll("(?s).*?(\\{.*\\}).*", "$1");

        try {
            return objectMapper.readValue(jsonLimpo, DadosCriarPontoIa.class);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao interpretar resposta da IA. Resposta recebida: " + respostaBruta, e);
        }
    }
}
