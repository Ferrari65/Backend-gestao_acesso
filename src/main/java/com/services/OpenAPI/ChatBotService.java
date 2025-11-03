package com.services.OpenAPI;

import com.dto.OpenAPI.OpenAIRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final WebClient openAiWebClient;

    @Value("${openai.model}")
    private String defaultModel;

    public String perguntar(String mensagemUsuario) {

        // Contexto fixo do assistente
        String sistema = """
                Você é um assistente virtual chamado TrackBot.
                Seu papel é ajudar usuários em dúvidas sobre sistemas, tecnologia e uso do sistema da Yasmin.
                Sempre responda em português do Brasil, de forma simples e educada.
                Se a pergunta envolver dados sensíveis (senhas, CPF, etc.), diga que não pode responder.
                """;

        String input = sistema + "\n\nUsuário: " + mensagemUsuario;

        // Corpo da requisição enviado à API da OpenAI
        OpenAIRequest requestBody = OpenAIRequest.builder()
                .model(defaultModel)
                .input(input)
                .build();

        try {
            String respostaJson = openAiWebClient.post()
                    .uri("/responses")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(
                            Retry.backoff(3, Duration.ofSeconds(5)) // tenta 3x, espera 5s, depois 10s, depois 20s
                                    .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests)
                                    .onRetryExhaustedThrow((spec, signal) ->
                                            new RuntimeException("Limite de requisições atingido. Tente mais tarde.")
                                    )
                    )
                    .block();

            return respostaJson;
        } catch (Exception e) {
            return "O assistente está temporariamente sobrecarregado. Tente novamente em alguns minutos.";
        }
    }
}
