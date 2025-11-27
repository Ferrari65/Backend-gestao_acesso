package com.services.impedimento;

import com.domain.user.Enum.SeveridadeImpedimento;
import com.domain.user.Impedimento.Impedimento;
import com.dto.impedimentos.ImpedimentoCreateRequest;
import com.dto.impedimentos.ImpedimentoResponse;
import com.repositories.impedimento.ImpedimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImpedimentoService {

    private final ImpedimentoRepository repo;

    @Transactional
    public ImpedimentoResponse criar(ImpedimentoCreateRequest req) {

        if (req.idViagem() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o idViagem.");
        }

        if (req.motivo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o motivo do impedimento.");
        }

        if (req.registradoPor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o líder que registrou o impedimento.");
        }

        var entity = Impedimento.builder()
                .motivo(req.motivo())
                .severidade(req.severidade() != null ? req.severidade() : SeveridadeImpedimento.MEDIA)
                .descricao(req.descricao())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .idViagem(req.idViagem())
                .ocorridoEm(OffsetDateTime.now())
                .registradoPor(req.registradoPor())
                .ativo(true)
                .build();

        entity = repo.save(entity);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ImpedimentoResponse> listar(Boolean apenasAtivos) {
        var list = (apenasAtivos != null && Boolean.TRUE.equals(apenasAtivos))
                ? repo.findByAtivoTrue()
                : repo.findAll();

        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ImpedimentoResponse inativar(UUID id) {
        var entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Impedimento não encontrado."));

        if (!entity.isAtivo()) {
            return toResponse(entity);
        }

        entity.setAtivo(false);
        entity.setTempoFinalizacao(OffsetDateTime.now());

        entity = repo.save(entity);
        return toResponse(entity);
    }

    private ImpedimentoResponse toResponse(Impedimento e) {
        return new ImpedimentoResponse(
                e.getIdImpedimento(),
                e.getMotivo(),
                e.getSeveridade(),
                e.getLatitude(),
                e.getLongitude(),
                e.getDescricao(),
                e.getIdViagem(),
                e.getOcorridoEm(),
                e.getRegistradoPor(),
                e.isAtivo(),
                e.getTempoFinalizacao()
        );
    }
}
