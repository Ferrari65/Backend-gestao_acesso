package com.services.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackPassRagService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public void indexarDocumentosTrackPass(List<String> textos) {
        List<Document> docs = textos.stream()
                .map(t -> new Document(t, Map.of("source", "trackpass_manual")))
                .toList();

        vectorStore.add(docs);
    }

    public String perguntarSobreTrackPass(String pergunta) {

        var searchRequest = SearchRequest.builder()
                .query(pergunta)
                .topK(5)
                .build();

        List<Document> similares = vectorStore.similaritySearch(searchRequest);

        String contexto = similares.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = """
            Você é o assistente do sistema TrackPass.
            Use apenas o contexto abaixo para responder.

            CONTEXTO:
            %s

            PERGUNTA:
            %s
            """.formatted(contexto, pergunta);

        return chatModel.call(prompt);
    }
}