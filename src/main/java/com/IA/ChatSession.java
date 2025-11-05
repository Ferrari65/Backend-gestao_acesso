package com.IA;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(
        value = WebApplicationContext.SCOPE_SESSION,
        proxyMode = ScopedProxyMode.TARGET_CLASS
)
@Data
public class ChatSession {

    private String etapaAtual = "MENU_PRINCIPAL";
    private String categoriaSelecionada;

    public void resetar() {
        this.etapaAtual = "MENU_PRINCIPAL";
        this.categoriaSelecionada = null;
    }
}