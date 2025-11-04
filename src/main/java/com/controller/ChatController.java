package com.controller;

import com.services.impl.RotaServiceImpl;
import com.services.rag.TrackPassRagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatModel chatModel;
    private final TrackPassRagService ragService;
    private final RotaServiceImpl rotaService;

    @PostMapping("/index")
    public String indexar(@RequestBody List<String> textos) {
        ragService.indexarDocumentosTrackPass(textos);
        return " Documentos indexados com sucesso!";
    }

    @GetMapping
    public String chat(@RequestParam String mensagem) {

        String lower = mensagem.toLowerCase();

        if (lower.contains("quantas rotas") ||
                lower.contains("total de rotas") ||
                lower.contains("número de rotas") ||
                lower.contains("qtd de rotas")) {

            long total = rotaService.contarRotas();
            return "Atualmente o sistema possui " + total + " rotas cadastradas.";
        }

        String prompt = """
            Você é um assistente do sistema TrackPass de gestão de acesso corporativo. Responda a Bom dia APENAS Quando te mandarem bom dia e tudo em de forma educada, tirando isso,
            Responda apenas perguntas relacionadas ao sistema TrackPass:
            - embarques
            - rotas
            - viagens
            - registros de acesso
            - portaria
            Se a pergunta não for sobre o sistema TrackPass, diga:
            "Desculpe, só posso responder sobre o sistema TrackPass de gestão de acesso."
            """;

        return chatModel.call(prompt + "\n\nPergunta: " + mensagem);
    }
}