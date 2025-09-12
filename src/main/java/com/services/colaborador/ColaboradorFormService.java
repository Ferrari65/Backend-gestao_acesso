package com.services.colaborador;

import com.domain.user.Enum.Periodo;
import com.domain.user.Enum.StatusForm;
import com.domain.user.colaborador.ColaboradorForm;
import com.dto.colaborador.FormCreateRequest;
import com.dto.colaborador.FormResponse;
import com.repositories.Colaborador.ColaboradorFormRepository;
import com.repositories.localizacao.CidadeRepository;
import com.repositories.localizacao.PontosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColaboradorFormService {

    private final ColaboradorFormRepository repo;
    private final PontosRepository pontoRepo;
    private final CidadeRepository cidadeRepo;

    public FormResponse criarPara(UUID idColaborador, FormCreateRequest req) {

        repo.findFirstByIdColaboradorAndTurnoAndStatusIn(
                idColaborador, req.turno(), List.of(StatusForm.PENDENTE, StatusForm.LIBERADO)
        ).ifPresent(x -> { throw new IllegalStateException("Já existe um formulário ativo para este turno."); });
        var cf = new ColaboradorForm();
        cf.setIdColaborador(idColaborador);
        cf.setNome(req.nome());
        cf.setMatricula(req.codigo());
        cf.setEnderecoRua(req.enderecoRua());
        cf.setBairro(req.bairro());
        cf.setIdCidade(req.idCidade());
        cf.setIdPonto(req.idPonto());
        cf.setTurno(req.turno());
        cf.setStatus(StatusForm.PENDENTE);
        cf.setCriadoEm(OffsetDateTime.now());

        try {
            var saved = repo.save(cf);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Formulário duplicado para este turno.", e);
        }
    }

    public List<FormResponse> meusFormularios(UUID idColaborador) {
        return repo.findByIdColaborador(idColaborador).stream().map(this::toResponse).toList();
    }

    public List<FormResponse> listar(Integer idCidade, Periodo turno, StatusForm status) {
        return repo.buscarComFiltros(idCidade, turno, status).stream().map(this::toResponse).toList();
    }

    public FormResponse atualizarStatus(UUID idForm, StatusForm novoStatus) {
        var cf = repo.findById(idForm).orElseThrow(() -> new NoSuchElementException("Form não encontrado"));
        if (novoStatus == StatusForm.PENDENTE) throw new IllegalArgumentException("Não é permitido voltar para PENDENTE.");
        cf.setStatus(novoStatus);
        return toResponse(repo.save(cf));
    }

    private FormResponse toResponse(ColaboradorForm c) {
        return new FormResponse(
                c.getId(), c.getIdColaborador(), c.getNome(), c.getMatricula(),
                c.getEnderecoRua(), c.getBairro(), c.getIdCidade(), c.getIdPonto(),
                c.getTurno(), c.getStatus(), c.getCriadoEm()
        );
    }
}
