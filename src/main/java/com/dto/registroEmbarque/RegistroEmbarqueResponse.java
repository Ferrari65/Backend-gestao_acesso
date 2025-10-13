package com.dto.registroEmbarque;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEmbarqueResponse {
    private UUID idEmbarque;
    private UUID idViagem;
    private UUID idColaborador;
    private String status;
    private String metodo;
    private boolean temAvisoPrevio;
    private UUID idAvisoPrevio;
    private OffsetDateTime dataEmbarque;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;
}