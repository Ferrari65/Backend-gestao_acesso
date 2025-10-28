package com.services.impedimento;

import com.domain.user.Impedimento.Impedimento;
import com.dto.impedimentos.ImpedimentoCreateRequest;
import com.dto.impedimentos.ImpedimentoResponse;
import com.repositories.impedimento.ImpedimentoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImpedimentoService {

    private final ImpedimentoRepository repo;

    @Transactional
    public ImpedimentoResponse criar(ImpedimentoCreateRequest req) {
        if (req.idViagem() == null && req.idVeiculo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe idViagem ou idVeiculo.");
        }
        var entity = Impedimento.builder()
                .motivo(req.motivo())
                .severidade(req.severidade())
                .descricao(req.descricao())
                .idViagem(req.idViagem())
                .idVeiculo(req.idVeiculo())
                .ocorridoEm(req.ocorridoEm())
                .registroPor(req.registradoPor())
                .ativo(true)
                .build();
        entity = repo.save(entity);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ImpedimentoResponse> listar(Boolean apenasAtivos) {
        var list = (apenasAtivos != null && apenasAtivos) ? repo.findByAtivoTrue() : repo.findAll();
        return list.stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList()); // Linha Corrigida
    }

    @Transactional
    public ImpedimentoResponse inativar(UUID id) {
        var entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Impedimento não encontrado"));
        if (!entity.isAtivo()) return toResponse(entity);
        entity.setAtivo(false);
        entity.setTempoFinalizacao(OffsetDateTime.now());
        return toResponse(entity);
    }

    private ImpedimentoResponse toResponse(Impedimento e) {
        return new ImpedimentoResponse(
                e.getIdImpedimento(),
                e.getMotivo(),
                e.getSeveridade(),
                e.getDescricao(),
                e.getIdViagem(),
                e.getIdVeiculo(),
                e.getOcorridoEm(),
                e.getRegistroPor(),
                e.isAtivo(),
                e.getTempoFinalizacao()
        );
    }
}
