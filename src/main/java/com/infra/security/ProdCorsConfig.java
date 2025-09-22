package com.infra.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Profile("prod")
public class ProdCorsConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.cors")
    public CorsProps corsProps() { return new CorsProps(); }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsProps props) {
        var c = new CorsConfiguration();

        for (String origin : props.getAllowedOrigins()) {
            if (origin.contains("*")) c.addAllowedOriginPattern(origin);
            else c.addAllowedOrigin(origin);
        }

        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","X-Requested-With"));
        c.setExposedHeaders(props.getExposeHeaders() == null ? List.of() : props.getExposeHeaders());
        c.setAllowCredentials(Boolean.TRUE.equals(props.getAllowCredentials()));
        c.setMaxAge(props.getMaxAge() == null ? 3600L : props.getMaxAge());

        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }

    @Getter
    @Setter
    public static class CorsProps {
        private List<String> allowedOrigins;
        private List<String> exposeHeaders;
        private Boolean allowCredentials;
        private Long maxAge;
    }
}