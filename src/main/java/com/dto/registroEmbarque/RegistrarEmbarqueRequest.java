package com.dto.registroEmbarque;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarEmbarqueRequest {

    private String identificador;
    private String metodo;
}
