package com.controller;

import com.domain.user.Enum.Periodo;
import com.domain.user.Rotas.Rota;
import com.domain.user.endereco.Pontos;
import com.dto.IA.ponto.CriarPontoIAResult;
import com.dto.IA.rota.RotaIARequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.IAService.pontos.PontoIaAutomationService;
import com.services.impl.RegistroEmbarqueServiceImpl;
import com.services.impl.RotaServiceImpl;
import com.services.rag.TrackPassRagService;
import com.services.registroEmbarque.ConsultaEmbarqueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final PontoIaAutomationService pontoIaAutomationService;
    private final ChatModel chatModel;
    private final TrackPassRagService ragService;
    private final RotaServiceImpl rotaService;
    private final RegistroEmbarqueServiceImpl registroEmbarqueService;
    private final ConsultaEmbarqueService consultaEmbarqueService;
    private final ObjectMapper objectMapper;

    // ---------------- PROMPT PARA CRIAR ROTA ----------------
    private static final String SYSTEM_PROMPT_ROTA_IA = """
Você é um assistente do sistema TrackPass.

Sua tarefa é:
Receber uma descrição em linguagem natural de uma rota de ônibus e retornar APENAS um JSON válido, sem explicações, exatamente no seguinte formato:

{
  "idCidade": null,
  "cidadeNome": null,
  "nome": "",
  "periodo": null,
  "capacidade": null,
  "ativo": true,
  "horaPartida": null,
  "horaChegada": null
}

REGRAS IMPORTANTES:

1) Não invente informações.
- Use APENAS os dados que o usuário mencionar.
- Se o usuário não falar um campo, preencha com null, exceto "ativo", que pode ser true por padrão.
- NUNCA use o horário atual do sistema. Se o usuário não disser um horário, deixe o campo como null.

2) Campos de cidade:
- Se o usuário informar um código de cidade (ex.: "cidade 3" ou "(3)"), preencha "idCidade" com esse número e deixe "cidadeNome" null.
- Se o usuário informar apenas o nome da cidade (ex.: "São Joaquim da Barra"), preencha "cidadeNome" com o texto exato e deixe "idCidade" como null.

3) Demais campos:
- "nome": nome exato da rota (ex.: "Rota T").
- "periodo": converta o que o usuário disser para um destes valores EXATOS:
  - "MANHA", "TARDE", "NOITE", "MADRUGADA"
  Exemplos:
  - "manhã", "de manhã" → "MANHA"
  - "tarde", "à tarde" → "TARDE"
  - "noite", "à noite", "noturno" → "NOITE"
  - "madrugada" → "MADRUGADA"
  Se o usuário não falar nada sobre período, use null.

- "capacidade": número de lugares, se o usuário informar (ex.: 44). Senão, null.
- "ativo": use o valor dito pelo usuário ("ativa", "inativa"). Se não falar nada, use true.
- "horaPartida": se o usuário informar (ex.: "7:10", "07h10"), converta para "HH:mm". Se não informar, use null.
- "horaChegada": idem, converta para "HH:mm" se informado, senão null.

4) Não inclua campos de pontos ou trajeto.

5) Resposta:
- Responda sempre SOMENTE com o JSON.
- Não escreva explicações, texto extra, comentários ou frases fora do JSON.

6) caso o usuario pergunte como criar uma rota, mande um exemplo de frase para ele:
""";

    // ---------------- PROMPT PARA ATRIBUIR PONTO À ROTA ----------------
    private static final String SYSTEM_PROMPT_ROTA_PONTO_IA = """
Você é um assistente do sistema TrackPass.

Sua tarefa é:
Receber uma frase em linguagem natural onde o usuário quer ATRIBUIR um ponto a uma rota,
informando nome da rota, nome do ponto e a ordem, e retornar APENAS um JSON válido no formato:

{
  "nomeRota": "",
  "nomePonto": "",
  "ordem": 1
}

REGRAS:

1) "nomeRota":
   - Deve ser o nome da rota mencionada na frase.
   - Preserve o texto conforme o usuário disser, apenas remova espaços extras nas pontas.

2) "nomePonto":
   - Nome do ponto/parada mencionado na frase.

3) "ordem":
   - Deve ser um número inteiro correspondente à posição do ponto na rota (1, 2, 3, ...).
   - Se o usuário disser "primeira parada", "primeiro ponto" → 1
   - "segunda", "segundo" → 2; "terceira" → 3, etc.

4) Se o usuário não informar algum dado essencial (nome da rota, nome do ponto ou ordem), coloque null no campo faltante.

5) Resposta:
   - Responda SEMPRE SOMENTE com o JSON.
   - Não escreva explicações, comentários, texto antes ou depois do JSON.

EXEMPLOS:

Usuário: "Coloca o ponto Portaria Principal na Rota 01 Matutina como primeira parada"
Resposta:
{"nomeRota":"Rota 01 Matutina","nomePonto":"Portaria Principal","ordem":1}

Usuário: "adiciona o ponto Jardim Aeroporto na rota 5 como terceiro ponto"
Resposta:
{"nomeRota":"rota 5","nomePonto":"Jardim Aeroporto","ordem":3}
""";

    @PostMapping("/alimentacao")
    public String indexar(@RequestBody List<String> textos) {
        ragService.indexarDocumentosTrackPass(textos);
        return "Documentos indexados com sucesso!";
    }

    @GetMapping
    public String chat(@RequestParam String mensagem) {

        String lower = mensagem.toLowerCase();

        // -------------------- SAUDAÇÃO INICIAL + O QUE A IA FAZ --------------------
        boolean ehSaudacaoSimples =
                (lower.contains("oi") || lower.contains("olá") || lower.contains("ola")
                        || lower.contains("bom dia") || lower.contains("boa tarde") || lower.contains("boa noite"))
                        && !lower.contains("rota")
                        && !lower.contains("ponto")
                        && !lower.contains("embarque")
                        && !lower.contains("lider")
                        && !lower.contains("colaborador");

        if (ehSaudacaoSimples) {
            return """
Olá! 👋 Eu sou a assistente de IA do TrackPass.

Posso te ajudar com, por exemplo:
🚌 Criar rotas a partir de uma frase em português.
📍 Criar pontos de embarque a partir de um endereço.
🔗 Atribuir pontos a uma rota na ordem correta.
✅ Consultar rotas ativas e inativas.
👥 Ver qual rota tem mais colaboradores ou mais embarques hoje.
⚠️ Consultar embarques inválidos na semana.ultar embarques inválidos na semana.

Alguns exemplos de coisas que você pode digitar:
- "Quero criar uma rota em São Joaquim da Barra chamada Rota T, de manhã, saindo às 07:10 e chegando às 08:00, com 44 lugares."
- "Criar ponto chamado Portaria Principal na Rua São José, 250, São Joaquim da Barra - SP."
- "Coloca o ponto Ponto Tiradentes na rota Rota D como quarta parada."
- "Quantos colaboradores estão na rota A de manhã?"
- "Quem ainda não embarcou na rota A do período da manhã em São Joaquim da Barra (3)?"

Me diga o que você quer fazer e eu tento ajudar. 🙂
""";
        }

        // -------------------- AJUDA: COMO CRIAR UM PONTO --------------------
        boolean ehPerguntaComoCriarPonto =
                lower.contains("como criar um ponto") ||
                        lower.contains("como criar ponto") ||
                        lower.contains("como cadastrar um ponto") ||
                        lower.contains("como cadastrar ponto") ||
                        (lower.contains("como faço") && lower.contains("ponto") && (lower.contains("criar") || lower.contains("cadastrar") || lower.contains("registrar"))) ||
                        lower.contains("exemplo de ponto") ||
                        lower.contains("exemplo ponto") ||
                        lower.contains("modelo de ponto") ||
                        lower.contains("como registrar um ponto") ||
                        lower.contains("como registrar ponto");

        if (ehPerguntaComoCriarPonto) {
            return """
Para criar um ponto usando a IA, envie uma frase com o **nome do ponto** e o **endereço completo**, por exemplo:

"Criar ponto chamado Portaria Principal na Rua São José, 250, São Joaquim da Barra - SP, Brasil."

Outros exemplos que funcionam:
- "Quero cadastrar um ponto chamado Ponto do Posto na Avenida Brasil, 1020, Orlândia - SP, Brasil."
- "Cadastrar novo ponto Escola Municipal Pedro Álvares na Rua 7 de Setembro, 305, Ipuã - SP, Brasil."

Sempre tente informar: rua, número, cidade, estado e país para que o endereço seja bem identificado.
""";
        }

        // -------------------- CRIAR PONTO --------------------
        boolean ehCriarPonto =
                (
                        lower.contains("criar ponto") ||
                                lower.contains("cadastrar ponto") ||
                                lower.contains("registrar ponto") ||
                                lower.contains("novo ponto") ||
                                lower.contains("ponto novo") ||
                                lower.contains("cadastrar novo ponto") ||
                                lower.contains("criar novo ponto") ||
                                lower.contains("ponto de embarque novo") ||
                                lower.contains("novo ponto de embarque")
                ) && !lower.contains("rota")
                        ||
                        (
                                lower.contains("criar parada") ||
                                        lower.contains("cadastrar parada") ||
                                        lower.contains("nova parada") ||
                                        lower.contains("parada nova")
                        ) && !lower.contains("rota");

        if (ehCriarPonto) {
            try {
                Pontos ponto = pontoIaAutomationService.criarPontoAPartirDeTexto(mensagem);

                String nomePonto = ponto.getNome();
                String endereco = ponto.getEndereco();
                String nomeCidade = ponto.getCidade() != null
                        ? ponto.getCidade().getNome()
                        : "cidade não informada";

                return "Ponto \"" + nomePonto + "\" criado com sucesso em "
                        + nomeCidade + ", no endereço " + endereco + ".";
            } catch (IllegalArgumentException e) {
                return "Não consegui criar o ponto porque o endereço parece incompleto ou genérico. " +
                        "Informe rua, número e cidade. Exemplo: " +
                        "\"Criar ponto chamado Ponto da escola na Rua São José, 250, São Joaquim da Barra - SP, Brasil\".";
            } catch (Exception e) {
                log.error("Erro ao criar ponto via IA", e);
                return "Tive um problema técnico ao tentar criar o ponto. Tente novamente mais tarde ou contate o suporte.";
            }
        }

        // -------------------- AJUDA: COMO ATRIBUIR PONTO À ROTA --------------------
        boolean ehPerguntaComoAtribuirPontoNaRota =
                (
                        lower.contains("como atribuir") ||
                                lower.contains("como colocar") ||
                                lower.contains("como adicionar")
                )
                        && lower.contains("ponto")
                        && lower.contains("rota");

        if (ehPerguntaComoAtribuirPontoNaRota) {
            return """
Para atribuir um ponto a uma rota usando a IA, envie uma frase informando:

- Nome do ponto
- Nome da rota
- A ordem (posição) do ponto na rota

Por exemplo:
"Coloca o ponto Ponto Tiradentes na rota Rota D como quarta parada."

Outros exemplos:
- "Adicionar o ponto Portaria Principal na rota Rota 01 Matutina como primeira parada."
- "Coloca o ponto Jardim Aeroporto na rota Rota 05 Tarde como terceira parada."

Você pode usar termos como "primeira parada", "segunda parada", "terceiro ponto", "quarta parada" etc. 
A IA vai transformar isso em um número de ordem (1, 2, 3, 4...).
""";
        }

        // -------------------- ATRIBUIR PONTO A ROTA COM IA --------------------
        boolean ehAtribuirPontoNaRota =
                (lower.contains("atribuir ponto") && lower.contains("rota")) ||
                        (lower.contains("colocar ponto") && lower.contains("rota")) ||
                        (lower.contains("adicionar ponto") && lower.contains("rota")) ||
                        (lower.contains("ponto") && lower.contains("rota") && lower.contains("ordem")) ||
                        (lower.contains("ponto") && lower.contains("rota") && lower.contains("parada"));

        if (ehAtribuirPontoNaRota) {
            try {
                String respostaJson = chatModel.call(
                        SYSTEM_PROMPT_ROTA_PONTO_IA + "\n\nUsuário: " + mensagem
                );

                CriarPontoIAResult cmd =
                        objectMapper.readValue(respostaJson, CriarPontoIAResult.class);

                if (cmd.nomeRota() == null || cmd.nomePonto() == null || cmd.ordem() == null) {
                    return """
Para atribuir um ponto a uma rota, preciso que você informe:
- Nome da rota
- Nome do ponto
- A ordem (posição) do ponto na rota

Exemplos:
- "Coloca o ponto Portaria Principal na rota Rota 01 Matutina como primeira parada"
- "Adiciona o ponto Jardim Aeroporto na rota Rota 05 Tarde como terceiro ponto"
""";
                }

                Rota rotaAtualizada = rotaService.atribuirPontoPorNomes(cmd);

                StringBuilder sb = new StringBuilder();
                sb.append("Ponto \"")
                        .append(cmd.nomePonto())
                        .append("\" atribuído à rota \"")
                        .append(rotaAtualizada.getNome())
                        .append("\" na ordem ")
                        .append(cmd.ordem())
                        .append(".");

                return sb.toString();

            } catch (IllegalStateException e) {
                return e.getMessage();
            } catch (jakarta.persistence.EntityNotFoundException e) {

                return e.getMessage()
                        + "\n\nConfira se o nome da rota e do ponto existem no sistema. " +
                        "Se precisar, peça a lista de pontos ou rotas cadastradas para o administrador.";
            } catch (Exception e) {
                log.error("Erro ao atribuir ponto à rota via IA", e);
                return "Tive um problema ao tentar atribuir o ponto à rota com base na sua mensagem. " +
                        "Tente informar: nome da rota, nome do ponto e a ordem (posição).";
            }
        }

        // -------------------- AJUDA: COMO CRIAR UMA ROTA --------------------
        boolean ehPerguntaComoCriarRota =
                (lower.contains("como criar uma rota")) ||
                        (lower.contains("como faço para criar rota")) ||
                        (lower.contains("como faço") && lower.contains("criar") && lower.contains("rota")) ||
                        (lower.contains("exemplo de rota")) ||
                        (lower.contains("exemplo rota")) ||
                        (lower.contains("modelo de rota"));

        if (ehPerguntaComoCriarRota) {
            return """
Para criar uma rota usando a IA, envie uma frase completa, por exemplo:

"Quero criar uma rota em São Joaquim da Barra chamada Rota T, no período da manhã, rota ativa, saindo às 07:10 e chegando às 08:00, com 44 lugares."

Você pode adaptar cidade, nome da rota, período, horários e quantidade de lugares conforme a sua necessidade.
""";
        }

        // -------------------- CRIAR ROTA  --------------------
        boolean ehCriarRota =
                lower.contains("rota") && (
                        lower.contains("criar") ||
                                lower.contains("cadastrar") ||
                                lower.contains("nova") ||
                                lower.contains("montar") ||
                                lower.contains("configurar")
                );

        if (ehCriarRota) {
            try {
                String respostaJson = chatModel.call(
                        SYSTEM_PROMPT_ROTA_IA + "\n\nUsuário: " + mensagem
                );

                RotaIARequestDTO dto = objectMapper.readValue(respostaJson, RotaIARequestDTO.class);
                Rota rota = rotaService.criarBasico(dto);
                String nomeCidade = rota.getCidade() != null
                        ? rota.getCidade().getNome()
                        : "cidade não informada";

                String periodoTexto = rota.getPeriodo() != null
                        ? rota.getPeriodo().name().toLowerCase()
                        : "período não informado";

                StringBuilder sb = new StringBuilder();
                sb.append("Rota \"")
                        .append(rota.getNome())
                        .append("\" criada com sucesso em ")
                        .append(nomeCidade)
                        .append(", período ")
                        .append(periodoTexto);

                if (rota.getHoraPartida() != null) {
                    sb.append(", saída às ").append(rota.getHoraPartida());
                }
                if (rota.getHoraChegada() != null) {
                    sb.append(", chegada às ").append(rota.getHoraChegada());
                }
                if (rota.getCapacidade() != null) {
                    sb.append(", capacidade de ").append(rota.getCapacidade()).append(" lugares");
                }

                sb.append(".");

                return sb.toString();

            } catch (Exception e) {
                log.error("Erro ao criar rota via IA", e);
                return "Tive um problema ao tentar criar a rota com base na sua mensagem. " +
                        "Confira se você informou os dados corretos e tente novamente." +
                        " Exemplo: Quero criar uma rota em São Joaquim da Barra chamada Rota T, no período da manhã, rota ativa, saindo às 07:10 e chegando às 08:00, com 44 lugares.";
            }
        }

        // -------------------- ROTAS  --------------------
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

    // -------------------- HELPERS --------------------

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
