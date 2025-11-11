package com.services.IAService.pontos;

import com.domain.user.endereco.Pontos;
import com.dto.localizacao.Ponto.DadosCriarPontoIa;
import com.dto.localizacao.Ponto.PontosRequestDTO;
import com.services.IAService.geocode.GeocodingService;
import com.services.localizacao.PontoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PontoIaAutomationServiceImpl implements PontoIaAutomationService {

    private final IaInterpretadorPontoService iaInterpretadorPontoService;
    private final GeocodingService geocodingService;
    private final PontoService pontoService;

    @Override
    public Pontos criarPontoAPartirDeTexto(String comandoUsuario) {

        DadosCriarPontoIa dados = iaInterpretadorPontoService.extrairDados(comandoUsuario);
        log.info("Dados extraídos pela IA: nomePonto='{}', endereco='{}', idCidade={}",
                dados.nomePonto(), dados.endereco(), dados.idCidade());

        if (dados.nomePonto() == null || dados.nomePonto().isBlank()) {
            throw new IllegalArgumentException("Nome do ponto não informado pela IA.");
        }

        if (dados.endereco() == null || dados.endereco().isBlank()) {
            throw new IllegalArgumentException("Endereço não informado pela IA.");
        }

        if (enderecoRuim(dados.endereco())) {
            throw new IllegalArgumentException("Endereço considerado inválido: " + dados.endereco());
        }

        if (dados.idCidade() == null) {
            throw new IllegalArgumentException("idCidade não informado pela IA.");
        }

        var coords = geocodingService.geocodificar(dados.endereco());
        PontosRequestDTO dto = new PontosRequestDTO(
                dados.idCidade(),
                dados.nomePonto().trim(),
                dados.endereco().trim(),
                coords.latitude(),
                coords.longitude()
        );

        log.info("Enviando DTO para PontoService.criar: idCidade={}, nome='{}', endereco='{}', lat={}, lng={}",
                dto.idCidade(), dto.nome(), dto.endereco(), dto.latitude(), dto.longitude());
        return pontoService.criar(dto);
    }

    private boolean enderecoRuim(String endereco) {
        String e = endereco == null ? "" : endereco.trim();

        if (e.length() < 10) return true;

        if (e.contains(", ,")) return true;

        if (!e.matches(".*\\d+.*")) return true;

        String[] parts = e.split(",");
        int filled = 0;
        for (String p : parts) {
            if (!p.trim().isEmpty()) {
                filled++;
            }
        }

        if (filled < 3) return true;
        String lower = e.toLowerCase();
        if (lower.equals("brasil") || lower.endsWith("- brasil") || lower.startsWith("praça, -")) {
            return true;
        }
        return false;
    }
}
