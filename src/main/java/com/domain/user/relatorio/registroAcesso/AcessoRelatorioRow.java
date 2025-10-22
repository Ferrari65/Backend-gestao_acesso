package com.domain.user.relatorio.registroAcesso;

import com.domain.user.Enum.TipoPessoa;

import java.time.LocalDate;
import java.time.LocalTime;

public record AcessoRelatorioRow(
        TipoPessoa tipoPessoa,
        String condutorNome,
        String condutorId,
        Short codPortaria,
        LocalDate dataEntrada,
        LocalTime horaEntrada,
        LocalDate dataSaida,
        LocalTime horaSaida,
        String situacao,
        String ocupantes,
        String observacao
) {}