package com.controller;

import com.services.OpenAPI.ChatBotService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatBotService chatGptService;

    @PostMapping
    public ResponseEntity<ChatResponse> conversar(@RequestBody ChatRequest request) {

        String mensagemUsuario = request.getMensagem();

        String resposta = chatGptService.perguntar(mensagemUsuario);

        ChatResponse resp = new ChatResponse();
        resp.setResposta(resposta);

        return ResponseEntity.ok(resp);
    }

    @Data
    public static class ChatRequest {
        private String mensagem;
    }

    @Data
    public static class ChatResponse {
        private String resposta;
    }
}
