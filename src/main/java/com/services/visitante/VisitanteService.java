package com.services.visitante;

import com.domain.user.visitante.Visitante;
import com.dto.PATCH.VisitanteAtivoPatchRequest;
import com.dto.visitante.VisitanteCreateRequest;
import com.dto.visitante.VisitanteResponse;
import com.repositories.visitante.VisitanteRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitanteService {

    private final VisitanteRepository repo;

    @Transactional
    public VisitanteResponse criar(@Valid VisitanteCreateRequest req) {
        if (repo.existsByTipoDocumentoAndNumeroDocumento(req.tipoDocumento(), req.numeroDocumento())) {
            throw new EntityExistsException("Documento já cadastrado para outro visitante");
        }

        Visitante entidade = Visitante.builder()
                .nomeCompleto(req.nomeCompleto())
                .tipoDocumento(req.tipoDocumento())
                .numeroDocumento(req.numeroDocumento())
                .dataNascimento(req.dataNascimento())
                .telefone(req.telefone())
                .empresaVisitante(req.empresaVisitante())
                .pessoaAnfitria(req.pessoaAnfitria())
                .motivoVisita(req.motivoVisita())
                .ativo(req.ativo() == null ? true : req.ativo())
                .build();

        entidade = repo.save(entidade);
        return toResponse(entidade);
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponse> listar(Boolean somenteAtivos) {
        Sort sort = Sort.by(Sort.Order.asc("nomeCompleto"));
        List<Visitante> lista = (somenteAtivos == null)
                ? repo.findAll(sort)
                : repo.findAllByAtivo(somenteAtivos, sort);

        return lista.stream().map(this::toResponse).toList();
    }

    @Transactional
    public VisitanteResponse patchAtivo(UUID id, @Valid VisitanteAtivoPatchRequest body) {
        Visitante v = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Visitante não encontrado"));
        v.setAtivo(body.ativo());
        return toResponse(v);
    }

    private VisitanteResponse toResponse(Visitante v) {
        return new VisitanteResponse(
                v.getId(),
                v.getNomeCompleto(),
                v.getTipoDocumento(),
                v.getNumeroDocumento(),
                v.getDataNascimento(),
                v.getTelefone(),
                v.getEmpresaVisitante(),
                v.getPessoaAnfitria(),
                v.getMotivoVisita(),
                v.isAtivo()
        );
    }
}