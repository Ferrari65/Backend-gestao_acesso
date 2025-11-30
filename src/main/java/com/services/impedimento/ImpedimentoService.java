package com.services.impedimento;

import com.domain.user.Enum.SeveridadeImpedimento;
import com.domain.user.Impedimento.Impedimento;
import com.domain.user.Rotas.Rota;
import com.domain.user.Rotas.RotaColaborador;
import com.domain.user.viagemRota.ViagemRota;
import com.domain.user.motorista.Motorista;
import com.domain.user.veiculo.Veiculo;
import com.dto.impedimentos.ImpedimentoCreateRequest;
import com.dto.impedimentos.ImpedimentoDetalhadoResponse;
import com.dto.impedimentos.ImpedimentoResponse;
import com.dto.mapa.ImpedimentoMapaResponse;
import com.repositories.Motorista.MotoristaRepository;
import com.repositories.Rota.RotaColaboradorRepository;
import com.repositories.Rota.RotaRepository;
import com.repositories.impedimento.ImpedimentoRepository;
import com.repositories.veiculo.VeiculoRepository;
import com.repositories.viagem.ViagemRepository;
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
    private final ViagemRepository viagemRotaRepository;
    private final MotoristaRepository motoristaRepository;
    private final VeiculoRepository veiculoRepository;
    private final RotaRepository rotaRepository;
    private final RotaColaboradorRepository rotaColaboradorRepository;

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

    @Transactional(readOnly = true)
    public List<ImpedimentoMapaResponse> listarParaMapa(SeveridadeImpedimento severidade, Boolean apenasAtivos) {
        List<Impedimento> list;

        if (severidade != null && Boolean.TRUE.equals(apenasAtivos)) {
            list = repo.findBySeveridadeAndAtivoTrue(severidade);
        } else if (severidade != null) {
            list = repo.findBySeveridade(severidade);
        } else if (Boolean.TRUE.equals(apenasAtivos)) {
            list = repo.findByAtivoTrue();
        } else {
            list = repo.findAll();
        }

        return list.stream()
                .map(this::toMapaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ImpedimentoDetalhadoResponse buscarDetalhado(UUID impedimentoId) {
        Impedimento imp = repo.findById(impedimentoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Impedimento não encontrado."
                ));

        ViagemRota viagem = viagemRotaRepository.findById(imp.getIdViagem())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Viagem associada ao impedimento não encontrada."
                ));

        Motorista motorista = null;
        if (viagem.getIdMotorista() != null) {
            motorista = motoristaRepository
                    .findById(Long.valueOf(viagem.getIdMotorista()))
                    .orElse(null);
        }

        Veiculo veiculo = null;
        if (viagem.getIdVeiculo() != null) {
            veiculo = veiculoRepository
                    .findById(Long.valueOf(viagem.getIdVeiculo()))
                    .orElse(null);
        }

        Rota rota = null;
        if (viagem.getIdRota() != null) {
            rota = rotaRepository
                    .findById(viagem.getIdRota())
                    .orElse(null);
        }

        List<RotaColaborador> rotaColaboradores =
                rotaColaboradorRepository.findByRota_IdRota(viagem.getIdRota());


        var colabsDto = rotaColaboradores.stream()
                .map(rc -> {
                    var c = rc.getColaborador();
                    return new ImpedimentoDetalhadoResponse.ColaboradorItem(
                            c.getIdColaborador(),   // ajuste se o nome do ID for diferente
                            c.getNome(),
                            c.getMatricula()
                    );
                })
                .toList();

        return new ImpedimentoDetalhadoResponse(
                imp.getIdImpedimento(),
                imp.getMotivo().name(),
                imp.getSeveridade().name(),
                imp.getDescricao(),
                imp.getLatitude(),
                imp.getLongitude(),
                imp.getOcorridoEm(),
                imp.isAtivo(),
                imp.getIdViagem(),
                viagem.getIdRota(),
                viagem.getIdMotorista(),
                viagem.getIdVeiculo(),
                motorista != null ? motorista.getNome() : null,
                rota != null ? rota.getNome() : null,
                colabsDto
        );
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

    private ImpedimentoMapaResponse toMapaResponse(Impedimento e) {
        return new ImpedimentoMapaResponse(
                e.getIdImpedimento(),
                e.getLatitude(),
                e.getLongitude(),
                e.getSeveridade(),
                e.getMotivo(),
                e.getDescricao(),
                e.getOcorridoEm(),
                e.isAtivo()
        );
    }
}
