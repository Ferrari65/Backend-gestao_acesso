package com.services.registroEmbarque;

import com.domain.user.Enum.Periodo;
import com.domain.user.colaborador.User;
import com.repositories.registroEmbarque.RegistroEmbarqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaEmbarqueService {

    private final RegistroEmbarqueRepository registroEmbarqueRepository;

    public List<User> buscarNaoEmbarcadosHojePorRota(Integer idRota) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        LocalDate hoje = LocalDate.now(zoneId);

        OffsetDateTime inicioDia = hoje.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime fimDia = hoje.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();

        return registroEmbarqueRepository.buscarNaoEmbarcadosNaRotaHoje(
                idRota,
                inicioDia,
                fimDia
        );
    }

    public List<User> buscarNaoEmbarcadosHojePorNomePeriodoCidade(
            String nomeRota,
            Periodo periodo,
            Integer idCidade
    ) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        LocalDate hoje = LocalDate.now(zoneId);

        OffsetDateTime inicioDia = hoje.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime fimDia = hoje.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();

        return registroEmbarqueRepository.buscarNaoEmbarcadosHojePorRotaPeriodoCidade(
                nomeRota,
                periodo,
                idCidade,
                inicioDia,
                fimDia
        );
    }
}