package com.services.IAService.geocode;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class GoogleGeocodingServiceImpl implements GeocodingService {

    private final WebClient.Builder webClientBuilder;

    @Value("${google.maps.geocoding.url}")
    private String geocodingUrl;

    @Value("${google.maps.geocoding.key}")
    private String apiKey;

    @Override
    public Coordenadas geocodificar(String enderecoCompleto) {
        try {
            String encodedAddress = URLEncoder.encode(enderecoCompleto, StandardCharsets.UTF_8);

            WebClient client = webClientBuilder.baseUrl(geocodingUrl).build();

            GoogleGeocodingResponse response = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("address", encodedAddress)
                            .queryParam("key", apiKey)
                            .queryParam("region", "br")
                            .queryParam("language", "pt-BR")
                            .build())
                    .retrieve()
                    .bodyToMono(GoogleGeocodingResponse.class)
                    .block();

            if (response == null) {
                throw new IllegalStateException("Resposta nula da API de geocodificação");
            }

            if (!"OK".equals(response.status()) || response.results().isEmpty()) {
                throw new IllegalStateException("Endereço não encontrado ou erro na API: status=" + response.status());
            }

            var location = response.results().get(0).geometry().location();
            return new Coordenadas(location.lat(), location.lng());

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao chamar a API de geocodificação", e);
        }
    }
}
