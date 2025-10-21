package com.dto.registroEmbarque;

import com.domain.user.colaborador.User;
import com.domain.user.registroEmbarque.RegistroEmbarque;
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
    private UUID validadorId;
    private String status;
    private String metodo;
    private boolean temAvisoPrevio;
    private UUID idAvisoPrevio;
    private OffsetDateTime dataEmbarque;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public static RegistroEmbarqueResponse from(RegistroEmbarque re) {
        UUID viagemId      = re.getViagem() != null ? re.getViagem().getIdViagem() : null;
        UUID colaboradorId = re.getColaborador() != null ? re.getColaborador().getIdColaborador() : null;
        UUID avisoPrevioId = re.getAvisoPrevio() != null ? re.getAvisoPrevio().getId() : null;

        return RegistroEmbarqueResponse.builder()
                .idEmbarque(re.getIdEmbarque())
                .idViagem(viagemId)
                .idColaborador(colaboradorId)
                .status(re.getStatusEmbarque() != null ? re.getStatusEmbarque().name() : null)
                .metodo(re.getMetodoValidacao() != null ? re.getMetodoValidacao().name() : null)
                .temAvisoPrevio(Boolean.TRUE.equals(re.getTemAvisoPrevio()))
                .idAvisoPrevio(avisoPrevioId)
                .dataEmbarque(re.getDataEmbarque())
                .criadoEm(re.getCriadoEm())
                .atualizadoEm(re.getAtualizadoEm())
                .build();
    }
}