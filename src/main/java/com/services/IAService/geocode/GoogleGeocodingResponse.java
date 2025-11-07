package com.services.IAService.geocode;

import java.math.BigDecimal;
import java.util.List;

public record GoogleGeocodingResponse(
        String status,
        List<Result> results
) {
    public record Result(Geometry geometry) {}
    public record Geometry(Location location) {}
    public record Location(BigDecimal lat, BigDecimal lng) {}
}