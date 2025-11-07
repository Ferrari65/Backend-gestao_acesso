package com.controller;

import com.domain.user.Enum.Periodo;
import com.domain.user.endereco.Pontos;
import com.services.IAService.pontos.PontoIaAutomationService;
import com.services.impl.RegistroEmbarqueServiceImpl;
import com.services.impl.RotaServiceImpl;
import com.services.rag.TrackPassRagService;
import com.services.registroEmbarque.ConsultaEmbarqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final PontoIaAutomationService pontoIaAutomationService;
    private final ChatModel chatModel;
    private final TrackPassRagService ragService;
    private final RotaServiceImpl rotaService;
    private final RegistroEmbarqueServiceImpl registroEmbarqueService;
    private final ConsultaEmbarqueService consultaEmbarqueService;

    @PostMapping("/alimentacao")
    public String indexar(@RequestBody List<String> textos) {
        ragService.indexarDocumentosTrackPass(textos);
        return "Documentos indexados com sucesso!";
    }

    @GetMapping
    public String chat(@RequestParam String mensagem) {

        String lower = mensagem.toLowerCase();

        // -------------------- CRIAR PONTO (IA + GEOCODING) --------------------
        boolean ehCriarPonto =
                lower.contains("criar ponto") ||
                        lower.contains("cadastrar ponto") ||
                        lower.contains("adicionar ponto") ||
                        lower.contains("novo ponto");

        if (ehCriarPonto) {
            try {
                Pontos ponto = pontoIaAutomationService.criarPontoAPartirDeTexto(mensagem);

                String nomePonto = ponto.getNome();
                String endereco = ponto.getEndereco();
                String nomeCidade = ponto.getCidade() != null
                        ? ponto.getCidade().getNome()
                        : "cidade não informada";

                // Resposta amigável para o usuário, sem detalhes técnicos
                return "Ponto \"" + nomePonto + "\" criado com sucesso em "
                        + nomeCidade + ", no endereço " + endereco + ".";
            } catch (Exception e) {
                return "Tive um problema ao tentar criar o ponto. Confira os dados e tente novamente, por favor.";
            }
        }

        // -------------------- ROTAS INFORMAÇÕES --------------------
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
                return "Atualmente o sistema possui " + total + " rotas cadastradas: "
                        + ativas + " ativas e " + inativas + " inativas.";
            }

            if ((lower.contains("mais") || lower.contains("maior"))
                    && (lower.contains("colaborador") || lower.contains("colaboradores"))) {
                return rotaService.montarMensagemRotaComMaisColaboradores();
            }

            if ((lower.contains("mais") || lower.contains("maior"))
                    && (lower.contains("embarque") || lower.contains("embarques"))
                    && lower.contains("hoje")) {
                return rotaService.montarMensagemRotaComMaisEmbarquesHoje();
            }

            if ((lower.contains("colaborador") || lower.contains("colaboradores"))
                    && (lower.contains("quantos") || lower.contains("quanto"))) {

                String nomeRota = extrairNomeCompletoDaRota(mensagem);
                Periodo periodo = extrairPeriodoDaMensagem(mensagem);

                if (nomeRota == null || nomeRota.isBlank()) {
                    return "Me informe o nome da rota. Exemplo: \"Quantos colaboradores estão na ROTA A de manhã?\"";
                }

                if (periodo == null) {
                    return "Me informe também o período (manhã, tarde ou noite) dessa rota.";
                }

                return rotaService.montarMensagemTotalColaboradoresPorRotaEPeriodo(nomeRota, periodo);
            }

            if (lower.contains("embarque")
                    && (lower.contains("invalido") || lower.contains("inválido")
                    || lower.contains("invalidos") || lower.contains("inválidos"))
                    && lower.contains("semana")) {

                return registroEmbarqueService.montarMensagemEmbarquesInvalidosSemanaAtual();
            }

            if (lower.contains("não embarcou") || lower.contains("nao embarcou")) {

                String nomeRota = extrairNomeRota(mensagem);
                Periodo periodo = extrairPeriodo(mensagem);
                Integer idCidade = extrairIdCidade(mensagem);

                if (nomeRota == null || periodo == null || idCidade == null) {
                    return """
                    Para essa consulta, preciso que você informe:
                    - Nome da rota (ex: "rota A")
                    - Período (manhã, tarde ou noite)
                    - Cidade com o código entre parênteses (ex: "São Joaquim da Barra (3)")
                    Exemplo completo:
                    "Quem ainda não embarcou na rota A do período da manhã em São Joaquim da Barra (3)?"
                    """;
                }

                var naoEmbarcados = consultaEmbarqueService
                        .buscarNaoEmbarcadosHojePorNomePeriodoCidade(nomeRota, periodo, idCidade);

                if (naoEmbarcados.isEmpty()) {
                    return "Todos os colaboradores da rota " + nomeRota
                            + ", período " + periodo
                            + ", na cidade informada já embarcaram hoje.";
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Os colaboradores que ainda não embarcaram na rota ")
                        .append(nomeRota)
                        .append(", período ")
                        .append(periodo)
                        .append(", na cidade informada são:\n");

                naoEmbarcados.forEach(colab -> {
                    sb.append("- ")
                            .append(colab.getNome());
                    if (colab.getMatricula() != null) {
                        sb.append(" (").append(colab.getMatricula()).append(")");
                    }
                    sb.append("\n");
                });

                return sb.toString();
            }
        }

        // -------------------- RESPOSTAS GERAIS --------------------
        String prompt = """
            Você é um assistente do sistema TrackPass de gestão de acesso corporativo.

            Sempre seja cordial.
            Diga Olá quando o usuário disser Olá.
            Responda Bom dia apenas quando o usuário disser Bom dia,
            Boa tarde apenas quando disser Boa tarde
            e Boa noite apenas quando disser Boa noite.

            Responda apenas perguntas relacionadas ao sistema TrackPass, como:
            - embarques
            - rotas
            - viagens
            - registros de acesso
            - portaria

            Se a pergunta não for sobre o sistema TrackPass, responda:
            "Desculpe, só posso responder sobre o sistema TrackPass de gestão de acesso."
            """;

        return chatModel.call(prompt + "\n\nPergunta: " + mensagem);
    }

    private String extrairNomeCompletoDaRota(String mensagemOriginal) {
        var pattern = Pattern.compile("(rota\\s+[\\p{L}\\p{N}_-]+)", Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(mensagemOriginal);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private Periodo extrairPeriodoDaMensagem(String mensagemOriginal) {
        String lower = mensagemOriginal.toLowerCase();

        if (lower.contains("manhã") || lower.contains("manha")) {
            return Periodo.MANHA;
        }
        if (lower.contains("tarde")) {
            return Periodo.TARDE;
        }
        if (lower.contains("noite")) {
            return Periodo.NOITE;
        }
        if (lower.contains("madrugada")) {
            return Periodo.MADRUGADA;
        }

        return null;
    }

    private String extrairNomeRota(String texto) {
        Matcher m = Pattern.compile("rota\\s+([A-Za-z0-9]+)", Pattern.CASE_INSENSITIVE)
                .matcher(texto);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private Periodo extrairPeriodo(String texto) {
        String lower = texto.toLowerCase();

        if (lower.contains("manhã") || lower.contains("manha")) {
            return Periodo.MANHA;
        }
        if (lower.contains("tarde")) {
            return Periodo.TARDE;
        }
        if (lower.contains("noite")) {
            return Periodo.NOITE;
        }
        return null;
    }

    private Integer extrairIdCidade(String texto) {
        Matcher m = Pattern.compile("\\((\\d+)\\)").matcher(texto);
        if (m.find()) {
            return Integer.valueOf(m.group(1));
        }
        return null;
    }
}
