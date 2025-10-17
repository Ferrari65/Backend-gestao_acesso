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

import java.time.OffsetDateTime;
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
    public RegistroEmbarqueResponse registrar(UUID idViagem, RegistrarEmbarqueRequest req, UUID idValidador) {

        ViagemRota viagem = viagemRepo.findById(idViagem)
                .orElseThrow(() -> new EntityNotFoundException("Viagem nao encontrada"));

        Integer idRota = viagem.getIdRota();
        var dataViagem = viagem.getData();

        String identificador = req.getIdentificador() == null ? "" : req.getIdentificador().trim();
        if (identificador.isBlank()) {
            throw new IllegalArgumentException("Identificador vazio");
        }
        User colaborador = userRepo.findByMatricula(identificador)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador nao encontrado"));

        boolean isLider = liderRotaRepo
                .existsByRota_IdRotaAndColaborador_IdColaboradorAndAtivoTrue(idRota, idValidador);
        if (!isLider) {
            throw new IllegalStateException("Usuario autenticado nao e lider ativo desta rota");
        }
        User validador = userRepo.findById(idValidador)
                .orElseThrow(() -> new EntityNotFoundException("Validador não encontrado"));

        if (repo.existsByViagemAndColaborador(viagem, colaborador)) {
            throw new IllegalArgumentException("Colaborador já possui registro nesta viagem.");
        }

        boolean pertence = rotaColabRepo
                .existsByColaborador_IdColaboradorAndRota_IdRota(colaborador.getIdColaborador(), idRota);

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
        StatusEmbarque status = (pertence || temAvisoPrevio)
                ? StatusEmbarque.EMBARCADO
                : StatusEmbarque.NAO_EMBARCOU;

        RegistroEmbarque reg = new RegistroEmbarque();
        reg.setViagem(viagem);
        reg.setColaborador(colaborador);
        reg.setValidador(validador);
        reg.setDataEmbarque(OffsetDateTime.now());
        reg.setStatusEmbarque(status);
        reg.setMetodoValidacao(parseMetodo(req.getMetodo()));
        reg.setTemAvisoPrevio(temAvisoPrevio);
        if (temAvisoPrevio) {
            reg.setAvisoPrevio(aviso);
        }

        reg = repo.save(reg);

        if (temAvisoPrevio && status == StatusEmbarque.EMBARCADO) {
            aviso.setUtilizado(true);
            aviso.setUtilizadoEm(OffsetDateTime.now());
            colabFormRepo.save(aviso);
        }

        return toResponse(reg);
    }

    private MetodoValidacao parseMetodo(String raw) {
        String norm = raw == null ? "" : raw.trim().toUpperCase();
        return switch (norm) {
            case "COD_BARRA", "CODIGO_BARRA", "CODBARRA" -> MetodoValidacao.COD_BARRA;
            case "MANUAL" -> MetodoValidacao.MANUAL;
            default -> throw new IllegalArgumentException("Método de validação inválido: " + raw);
        };
    }

    private RegistroEmbarqueResponse toResponse(RegistroEmbarque e) {
        return RegistroEmbarqueResponse.builder()
                .idEmbarque(e.getIdEmbarque())
                .idViagem(e.getViagem().getIdViagem())
                .idColaborador(e.getColaborador().getIdColaborador())
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
