package com.services.impl;

import com.domain.user.Enum.StatusForm;
import com.domain.user.colaborador.ColaboradorForm;
import com.dto.colaborador.ColaboradorDTO;
import com.dto.colaborador.FormCreateRequest;
import com.dto.colaborador.FormResponse;
import com.repositories.Colaborador.ColaboradorFormRepository;
import com.repositories.UserRepository;
import com.services.colaborador.ColaboradorFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColaboradorFormServiceImpl implements ColaboradorFormService {

    private final ColaboradorFormRepository repo;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public FormResponse criarPara(ColaboradorDTO colab, FormCreateRequest req) {
        if (req.idRotaDestino() == null) throw new IllegalArgumentException("idRotaDestino é obrigatório.");
        if (req.dataUso() == null || req.dataUso().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("dataUso deve ser hoje ou futuro.");
        if (req.turno() == null) throw new IllegalArgumentException("turno é obrigatório.");
        String motivo = req.motivo() == null ? "" : req.motivo().trim();
        if (motivo.length() < 10) throw new IllegalArgumentException("motivo muito curto (mín. 10 caracteres).");
        if (req.idRotaOrigem() != null && req.idRotaOrigem().equals(req.idRotaDestino()))
            throw new IllegalArgumentException("Rota destino não pode ser igual à rota atual.");

        Collection<StatusForm> ativos = List.of(StatusForm.LIBERADO, StatusForm.LIBERADO);

        repo.findFirstByIdColaboradorAndDataUsoAndIdRotaDestinoAndStatusIn(
                colab.idColaborador(), req.dataUso(), req.idRotaDestino(), ativos
        ).ifPresent(x -> { throw new IllegalStateException("Já existe um aviso ativo para essa data e rota."); });

        var usuario = userRepository.findById(colab.idColaborador()).orElse(null);
        String nome = (colab.nome() == null || colab.nome().isBlank()) ? (usuario != null ? usuario.getNome() : null) : colab.nome();
        String matricula = (colab.matricula() == null || colab.matricula().isBlank()) ? (usuario != null ? usuario.getMatricula() : null) : colab.matricula();

        var cf = ColaboradorForm.builder()
                .idColaborador(colab.idColaborador())
                .nome(nome)
                .matricula(matricula)
                .idRotaOrigem(req.idRotaOrigem())
                .idRotaDestino(req.idRotaDestino())
                .dataUso(req.dataUso())
                .turno(req.turno())
                .motivo(motivo)
                .status(StatusForm.LIBERADO)
                .criadoEm(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        try {
            var saved = repo.save(cf);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Aviso duplicado para essa data e rota.", e);
        }
    }

    @Override
    public List<FormResponse> listarTodosDoColaborador(UUID idColaborador, StatusForm status) {
        List<ColaboradorForm> lista = (status == null)
                ? repo.findByIdColaboradorOrderByCriadoEmDesc(idColaborador)
                : repo.findByIdColaboradorAndStatusOrderByCriadoEmDesc(idColaborador, status);
        return lista.stream().map(this::toResponse).toList();
    }

    @Override
    public List<FormResponse> listarTodos(StatusForm status) {
        List<ColaboradorForm> lista = (status == null)
                ? repo.findAllByOrderByCriadoEmDesc()
                : repo.findByStatusOrderByCriadoEmDesc(status);
        return lista.stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public FormResponse atualizarStatus(UUID idForm, StatusForm novoStatus, UUID idUsuarioAcionador) {
        throw new UnsupportedOperationException("Fluxo sem aprovação manual: alteração de status desabilitada.");
    }

    private FormResponse toResponse(ColaboradorForm c) {
        return new FormResponse(
                c.getId(),
                c.getIdColaborador(),
                c.getNome(),
                c.getMatricula(),
                c.getIdRotaOrigem(),
                c.getIdRotaDestino(),
                c.getDataUso(),
                c.getTurno(),
                c.getMotivo(),
                c.getStatus(),
                c.getCriadoEm()
        );
    }
}
