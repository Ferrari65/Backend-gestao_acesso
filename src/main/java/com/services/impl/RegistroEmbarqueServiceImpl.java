package com.services.impl;

import com.domain.user.Enum.StatusForm;
import com.domain.user.Enum.StatusEmbarque;
import com.domain.user.Enum.MetodoValidacao;
import com.domain.user.ViagemRota;
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
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.UUID;

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
                .orElseThrow(() -> new EntityNotFoundException("Viagem não encontrada"));

        Integer idRota = viagem.getIdRota();
        var dataViagem = viagem.getSaidaPrevista();

        String matricula = req.getIdentificador() == null ? "" : req.getIdentificador().trim();
        if (matricula.isBlank()) throw new IllegalArgumentException("Identificador (matrícula) vazio.");

        User colaborador = userRepo.findByMatricula(matricula)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado"));

        if (!liderRotaRepo.existsByRota_IdRotaAndColaborador_IdColaboradorAndAtivoTrue(idRota, idValidador)) {
            throw new IllegalStateException("Usuário autenticado não é líder ativo desta rota.");
        }
        User validador = userRepo.findById(idValidador)
                .orElseThrow(() -> new EntityNotFoundException("Validador não encontrado"));

        if (repo.existsByViagemAndColaborador(viagem, colaborador)) {
            throw new IllegalArgumentException("Colaborador já possui registro nesta viagem.");
        }

        boolean pertence = rotaColabRepo
                .existsByColaborador_IdColaboradorAndRota_IdRota(colaborador.getIdColaborador(), idRota);

        boolean temAvisoPrevio = false;
        UUID idAviso = null;

        if (!pertence) {
            var liberados = EnumSet.of(StatusForm.LIBERADO);
            var avisoOpt = colabFormRepo.findFirstByIdColaboradorAndDataUsoAndIdRotaDestinoAndStatusIn(
                    colaborador.getIdColaborador(), dataViagem, idRota, liberados
            );

            if (avisoOpt.isEmpty()) {
                RegistroEmbarque neg = new RegistroEmbarque();
                setBase(neg, viagem, colaborador, validador);
                setStatus(neg, StatusEmbarque.NAO_EMBARCADO);
                setMetodo(neg, parseMetodo(req.getMetodo()));
                setTemAvisoPrevio(neg, false);
                setIdAvisoPrevio(neg, null);

                neg = repo.save(neg);
                return toResponse(neg);
            }
            temAvisoPrevio = true;
            idAviso = avisoOpt.get().getId();
        }

        RegistroEmbarque ok = new RegistroEmbarque();
        setBase(ok, viagem, colaborador, validador);
        setStatus(ok, StatusEmbarque.EMBARQUE);
        setMetodo(ok, parseMetodo(req.getMetodo()));
        setTemAvisoPrevio(ok, temAvisoPrevio);
        setIdAvisoPrevio(ok, idAviso);

        ok = repo.save(ok);
        return toResponse(ok);
    }


    private void setBase(RegistroEmbarque e, ViagemRota v, User c, User val) {

        try { e.getClass().getMethod("setViagem", ViagemRota.class).invoke(e, v); } catch (Exception ignored) {}
        try { e.getClass().getMethod("setColaborador", User.class).invoke(e, c); } catch (Exception ignored) {}
        try { e.getClass().getMethod("setValidador", User.class).invoke(e, val); } catch (Exception ignored) {}
        try { e.getClass().getMethod("setDataEmbarque", OffsetDateTime.class).invoke(e, OffsetDateTime.now()); } catch (Exception ignored) {}
    }

    private void setStatus(RegistroEmbarque e, StatusEmbarque status) {
        try { e.getClass().getMethod("setStatusEmbarque", StatusEmbarque.class).invoke(e, status); }
        catch (Exception ex) {
            try { e.getClass().getMethod("setStatus", StatusEmbarque.class).invoke(e, status); }
            catch (Exception ignored) {}
        }
    }

    private void setMetodo(RegistroEmbarque e, MetodoValidacao metodo) {
        try { e.getClass().getMethod("setMetodoValidacao", MetodoValidacao.class).invoke(e, metodo); }
        catch (Exception ex) {
            try { e.getClass().getMethod("setMetodo", MetodoValidacao.class).invoke(e, metodo); }
            catch (Exception ignored) {}
        }
    }

    private void setTemAvisoPrevio(RegistroEmbarque e, boolean v) {
        try { e.getClass().getMethod("setTemAvisoPrevio", Boolean.class).invoke(e, v); }
        catch (NoSuchMethodException ex) {
            try { e.getClass().getMethod("setTemAvisoPrevio", boolean.class).invoke(e, v); }
            catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private void setIdAvisoPrevio(RegistroEmbarque e, UUID idAviso) {
        try { e.getClass().getMethod("setIdAvisoPrevio", UUID.class).invoke(e, idAviso); } catch (Exception ignored) {}
    }

    private MetodoValidacao parseMetodo(String raw) {
        var norm = raw == null ? "" : raw.trim().toUpperCase();
        return switch (norm) {
            case "COD_BARRA" -> MetodoValidacao.COD_BARRA;
            case "MANUAL"    -> MetodoValidacao.MANUAL;
            default -> throw new IllegalArgumentException("Método de validação inválido: " + raw);
        };
    }

    private RegistroEmbarqueResponse toResponse(RegistroEmbarque e) {
        RegistroEmbarqueResponse r = new RegistroEmbarqueResponse();

        try { r.setIdEmbarque((UUID) e.getClass().getMethod("getIdEmbarque").invoke(e)); } catch (Exception ignored) {}
        try { Object v = e.getClass().getMethod("getViagem").invoke(e);
            UUID id = (UUID) v.getClass().getMethod("getIdViagem").invoke(v);
            r.setIdViagem(id); } catch (Exception ignored) {}
        try { Object c = e.getClass().getMethod("getColaborador").invoke(e);
            UUID id = (UUID) c.getClass().getMethod("getIdColaborador").invoke(c);
            r.setIdColaborador(id); } catch (Exception ignored) {}

        try {
            Object st = null;
            try { st = e.getClass().getMethod("getStatusEmbarque").invoke(e); }
            catch (Exception ex) { st = e.getClass().getMethod("getStatus").invoke(e); }
            r.setStatus(st != null ? st.toString() : null);
        } catch (Exception ignored) {}

        try {
            Object m = null;
            try { m = e.getClass().getMethod("getMetodoValidacao").invoke(e); }
            catch (Exception ex) { m = e.getClass().getMethod("getMetodo").invoke(e); }
            r.setMetodo(m != null ? m.toString() : null);
        } catch (Exception ignored) {}

        try { r.setTemAvisoPrevio((Boolean) e.getClass().getMethod("getTemAvisoPrevio").invoke(e)); } catch (Exception ignored) {}
        try { r.setIdAvisoPrevio((UUID) e.getClass().getMethod("getIdAvisoPrevio").invoke(e)); } catch (Exception ignored) {}
        try { r.setDataEmbarque((OffsetDateTime) e.getClass().getMethod("getDataEmbarque").invoke(e)); } catch (Exception ignored) {}
        try { r.setCriadoEm((OffsetDateTime) e.getClass().getMethod("getCriadoEm").invoke(e)); } catch (Exception ignored) {}
        try { r.setAtualizadoEm((OffsetDateTime) e.getClass().getMethod("getAtualizadoEm").invoke(e)); } catch (Exception ignored) {}

        return r;
    }
}
