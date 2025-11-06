package com.services.impl;

import com.domain.user.Enum.MetodoValidacao;
import com.domain.user.Enum.StatusEmbarque;
import com.domain.user.Enum.StatusForm;
import com.domain.user.viagemRota.ViagemRota;
import com.domain.user.colaborador.ColaboradorForm;
import com.domain.user.colaborador.User;
import com.domain.user.registroEmbarque.RegistroEmbarque;
import com.dto.registroEmbarque.RegistrarEmbarqueRequest;
import com.dto.registroEmbarque.RegistroEmbarqueResponse;
import com.exceptions.RegistroEmbarqueException;
import com.repositories.Colaborador.ColaboradorFormRepository;
import com.repositories.Rota.RotaColaboradorRepository;
import com.repositories.UserRepository;
import com.repositories.liderRota.LiderRotaRepository;
import com.repositories.registroEmbarque.RegistroEmbarqueRepository;
import com.repositories.viagem.ViagemRepository;
import com.services.registroEmbarque.RegistroEmbarqueService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RegistroEmbarqueServiceImpl implements RegistroEmbarqueService {

    private final RegistroEmbarqueRepository repo;
    private final ViagemRepository viagemRepo;
    private final UserRepository userRepo;
    private final LiderRotaRepository liderRotaRepo;
    private final RotaColaboradorRepository rotaColabRepo;
    private final ColaboradorFormRepository colabFormRepo;

    @Transactional
    @Override
    public RegistroEmbarqueResponse registrar(UUID idViagem,
                                              RegistrarEmbarqueRequest req,
                                              UUID idValidador) {

        ViagemRota viagem = viagemRepo.findById(idViagem)
                .orElseThrow(() -> new EntityNotFoundException("Viagem nao encontrada"));

        Integer idRota = viagem.getIdRota();
        var dataViagem = viagem.getData();

        String identificador = req.getIdentificador() == null ? "" : req.getIdentificador().trim();
        if (identificador.isBlank()) {
            throw new RegistroEmbarqueException("Identificador do colaborador não pode ser vazio.");
        }

        User colaborador = userRepo.findByMatricula(identificador)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador nao encontrado"));

        boolean isLider = liderRotaRepo
                .existsByRota_IdRotaAndColaborador_IdColaboradorAndAtivoTrue(idRota, idValidador);
        if (!isLider) {
            throw new RegistroEmbarqueException("Usuário autenticado não é líder ativo desta rota.");
        }

        User validador = userRepo.findById(idValidador)
                .orElseThrow(() -> new EntityNotFoundException("Validador não encontrado"));

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        OffsetDateTime agora = OffsetDateTime.now(zone);

        OffsetDateTime inicioDia = agora
                .toLocalDate()
                .atStartOfDay(zone)
                .toOffsetDateTime();

        OffsetDateTime fimDia = inicioDia.plusDays(1);

        boolean jaRegistradoHoje = repo
                .existsByViagemAndColaboradorAndDataEmbarqueBetween(
                        viagem,
                        colaborador,
                        inicioDia,
                        fimDia
                );

        if (jaRegistradoHoje) {
            throw new RegistroEmbarqueException(
                    "Colaborador já possui registro de embarque hoje nesta viagem."
            );
        }
        boolean pertence = rotaColabRepo
                .existsByColaborador_IdColaboradorAndRota_IdRota(
                        colaborador.getIdColaborador(), idRota
                );

        ColaboradorForm aviso = null;
        if (!pertence) {
            Set<StatusForm> aceitos = EnumSet.of(StatusForm.LIBERADO);

            Optional<ColaboradorForm> exact = colabFormRepo
                    .findTopByIdColaboradorAndDataUsoAndIdRotaDestinoAndStatusInAndUtilizadoFalseOrderByCriadoEmDesc(
                            colaborador.getIdColaborador(), dataViagem, idRota, aceitos
                    );

            if (exact.isPresent()) {
                aviso = exact.get();
            } else {

                Optional<ColaboradorForm> byRoute = colabFormRepo
                        .findTopByIdColaboradorAndIdRotaDestinoAndStatusInAndUtilizadoFalseOrderByCriadoEmDesc(
                                colaborador.getIdColaborador(), idRota, aceitos
                        );
                if (byRoute.isPresent()) {
                    aviso = byRoute.get();
                }
            }
        }

        boolean temAvisoPrevio = (aviso != null);

        if (!pertence && !temAvisoPrevio) {
            throw new RegistroEmbarqueException(
                    "Colaborador não pertence à rota desta viagem e não possui aviso prévio liberado para esta rota."
            );
        }

        StatusEmbarque status = StatusEmbarque.EMBARCADO;

        RegistroEmbarque reg = new RegistroEmbarque();
        reg.setViagem(viagem);
        reg.setColaborador(colaborador);
        reg.setValidador(validador);
        reg.setDataEmbarque(agora);
        reg.setStatusEmbarque(status);
        reg.setMetodoValidacao(parseMetodo(req.getMetodo()));

        if (temAvisoPrevio) {
            reg.setTemAvisoPrevio(true);
            reg.setAvisoPrevio(aviso);

            aviso.setUtilizado(true);
            aviso.setUtilizadoEm(agora);
            colabFormRepo.save(aviso);
        } else {
            reg.setTemAvisoPrevio(false);
            reg.setAvisoPrevio(null);
        }

        reg = repo.save(reg);

        return toResponse(reg);
    }

    public String montarMensagemEmbarquesInvalidosSemanaAtual() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");

        LocalDate hoje = LocalDate.now(zone);
        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = inicioSemana.plusDays(7);

        OffsetDateTime inicio = inicioSemana.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime fim = fimSemana.atStartOfDay(zone).toOffsetDateTime();

        long totalInvalidos = repo.contarEmbarquesInvalidosPorPeriodo(inicio, fim);

        if (totalInvalidos == 0) {
            return "Nenhum embarque inválido foi registrado nesta semana.";
        }

        return "Nesta semana, foram registrados " + totalInvalidos + " embarques inválidos.";
    }

    private MetodoValidacao parseMetodo(String raw) {
        String norm = raw == null ? "" : raw.trim().toUpperCase();
        return switch (norm) {
            case "COD_BARRA", "CODIGO_BARRA", "CODBARRA" -> MetodoValidacao.COD_BARRA;
            case "MANUAL" -> MetodoValidacao.MANUAL;
            default -> throw new RegistroEmbarqueException("Método de validação inválido: " + raw);
        };
    }

    private RegistroEmbarqueResponse toResponse(RegistroEmbarque e) {
        return RegistroEmbarqueResponse.builder()
                .idEmbarque(e.getIdEmbarque())
                .idViagem(e.getViagem().getIdViagem())
                .idColaborador(e.getColaborador().getIdColaborador())
                .validadorId(e.getValidador() != null ? e.getValidador().getIdColaborador() : null)
                .status(e.getStatusEmbarque().name())
                .metodo(e.getMetodoValidacao().name())
                .temAvisoPrevio(Boolean.TRUE.equals(e.getTemAvisoPrevio()))
                .idAvisoPrevio(e.getAvisoPrevio() != null ? e.getAvisoPrevio().getId() : null)
                .dataEmbarque(e.getDataEmbarque())
                .criadoEm(e.getCriadoEm())
                .atualizadoEm(e.getAtualizadoEm())
                .build();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<RegistroEmbarqueResponse> listarTodos(UUID idViagem) {
        Specification<RegistroEmbarque> spec =
                (root, q, cb) -> cb.equal(root.join("viagem").get("idViagem"), idViagem);

        return repo.findAll(spec, Sort.by(Sort.Direction.DESC, "criadoEm"))
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
