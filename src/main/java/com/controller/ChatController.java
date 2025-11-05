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

    @PostMapping("/alimentacao")
    public String indexar(@RequestBody List<String> textos) {
        ragService.indexarDocumentosTrackPass(textos);
        return " Documentos indexados com sucesso!";
    }

    @GetMapping
    public String chat(@RequestParam String mensagem) {

        String lower = mensagem.toLowerCase();

        if (lower.contains("rota")) {

            if (lower.contains("ativa")) {
                long ativas = rotaService.contarRotasAtivas();
                return "Atualmente o sistema possui " + ativas + " rotas ativas.";
            }

            if (lower.contains("inativa")) {
                long inativas = rotaService.contarRotasInativas();
                return "Atualmente o sistema possui " + inativas + " rotas inativas.";
            }

            if (lower.contains("total") || lower.contains("quantas") || lower.contains("todas")) {
                long ativas = rotaService.contarRotasAtivas();
                long inativas = rotaService.contarRotasInativas();
                long total = ativas + inativas;
                return "Atualmente o sistema possui " + total + " rotas cadastradas: " +
                        ativas + " ativas e " + inativas + " inativas.";
            }

            if ( (lower.contains("mais") || lower.contains("maior")) &&
                    (lower.contains("colaborador") || lower.contains("colaboradores")) ) {

                return rotaService.montarMensagemRotaComMaisColaboradores();
            }
            if ((lower.contains("mais") || lower.contains("maior"))
                    && (lower.contains("embarque") || lower.contains("embarques"))
                    && lower.contains("hoje")) {

                return rotaService.montarMensagemRotaComMaisEmbarquesHoje();
            }

            if (lower.contains("rota") &&
                    (lower.contains("colaborador") || lower.contains("colaboradores")) &&
                    (lower.contains("quantos") || lower.contains("quanto"))) {

                String nomeRota = extrairNomeCompletoDaRota(mensagem); // ex: "ROTA A" ou "rota A"
                if (nomeRota != null && !nomeRota.isBlank()) {
                    return rotaService.montarMensagemTotalColaboradoresPorRota(nomeRota);
                } else {
                    return "Me informe o nome da rota. Exemplo: \"Quantos colaboradores estão na ROTA A?\"";
                }
            }
        }

        String prompt = """
            Você é um assistente do sistema TrackPass de gestão de acesso corporativo.Sempre seja cordial, diga Ola quando te disserem Ola. 
            Responda "Bom dia" APENAS quando te mandarem bom dia, e Boa noite APENAS quando te mandarem boa noite, e Boa Tarde Apenas quando te mandarem Boa tarde. sempre de forma educada.
            Fora isso, responda apenas perguntas relacionadas ao sistema TrackPass:
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

    private String extrairNomeCompletoDaRota(String mensagemOriginal) {
        var pattern = java.util.regex.Pattern.compile(
                "(rota\\s+[\\p{L}\\p{N}_-]+)",
                java.util.regex.Pattern.CASE_INSENSITIVE
        );
        var matcher = pattern.matcher(mensagemOriginal);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}