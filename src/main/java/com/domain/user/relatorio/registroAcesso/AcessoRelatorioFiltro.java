package com.domain.user.relatorio.registroAcesso;

import com.domain.user.Enum.TipoPessoa;

import java.time.LocalDate;

public record AcessoRelatorioFiltro(
        LocalDate de,
        LocalDate ate,
        TipoPessoa tipoPessoa,
        Short codPortaria,
        Boolean somenteAbertos,
        Boolean incluirOcupantes
) {
    public boolean isSomenteAbertos()     { return Boolean.TRUE.equals(somenteAbertos); }
    public boolean isIncluirOcupantes()   { return Boolean.TRUE.equals(incluirOcupantes); }
}