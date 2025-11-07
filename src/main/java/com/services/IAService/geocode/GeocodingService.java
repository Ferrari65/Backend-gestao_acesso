package com.services.IAService.geocode;

import java.math.BigDecimal;

public interface GeocodingService {
    Coordenadas geocodificar(String enderecoCompleto);
    record Coordenadas(BigDecimal latitude, BigDecimal longitude) {}
}