package com.services.IAService.pontos;

import com.domain.user.endereco.Pontos;
import com.dto.localizacao.Ponto.DadosCriarPontoIa;
import com.dto.localizacao.Ponto.PontosRequestDTO;
import com.services.IAService.geocode.GeocodingService;
import com.services.localizacao.PontoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PontoIaAutomationServiceImpl implements PontoIaAutomationService {

    private final IaInterpretadorPontoService iaInterpretadorPontoService;
    private final GeocodingService geocodingService;
    private final PontoService pontoService;

    @Override
    public Pontos criarPontoAPartirDeTexto(String comandoUsuario) {

        DadosCriarPontoIa dados = iaInterpretadorPontoService.extrairDados(comandoUsuario);

        var coords = geocodingService.geocodificar(dados.endereco());
        PontosRequestDTO dto = new PontosRequestDTO(
                dados.idCidade(),
                dados.nomePonto(),
                dados.endereco(),
                coords.latitude(),
                coords.longitude()
        );
        return pontoService.criar(dto);
    }
}