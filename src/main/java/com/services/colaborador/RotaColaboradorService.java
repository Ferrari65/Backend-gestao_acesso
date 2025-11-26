package com.services.colaborador;

import com.dto.colaborador.RotaColaboradorResponse;
import com.domain.user.Rotas.RotaColaborador;
import com.domain.user.Rotas.RotaColaboradorId;
import com.dto.mapa.MapaColabPontoDTO;
import com.exceptions.RegraNegocioException;
import com.repositories.Rota.RotaColaboradorRepository;
import com.repositories.Rota.RotaRepository;
import com.repositories.UserRepository;
import com.repositories.localizacao.PontosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RotaColaboradorService {

    private final RotaColaboradorRepository rotaColabRepo;
    private final RotaRepository rotaRepo;
    private final UserRepository userRepo;
    private final PontosRepository pontosRepo;

    @Transactional
    public RotaColaboradorResponse atribuir(Integer idRota, UUID idColaborador, Integer idPonto) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new NoSuchElementException("Rota não encontrada"));

        var user = userRepo.findById(idColaborador)
                .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));

        Boolean ativoUser = user.getAtivo();
        if (ativoUser == null || !ativoUser) {
            throw new RegraNegocioException("COLABORADOR_INATIVO", "Colaborador precisa estar ativo.");
        }

        boolean jaEmOutraRota = rotaColabRepo
                .existsByColaborador_IdColaboradorAndId_IdRotaNotAndAtivoTrue(idColaborador, idRota);

        if (jaEmOutraRota) {
            throw new RegraNegocioException(
                    "COLABORADOR_JA_ATRIBUIDO",
                    "Colaborador já está atribuído a outra rota."
            );
        }
        var existenteOpt = rotaColabRepo.findById_IdRotaAndId_IdColaborador(idRota, idColaborador);

        if (existenteOpt.isPresent()) {
            var existente = existenteOpt.get();

            if (Boolean.FALSE.equals(existente.getAtivo())) {
                existente.setAtivo(true);
            }

            if (idPonto != null) {
                var ponto = pontosRepo.findById(idPonto)
                        .orElseThrow(() -> new NoSuchElementException("Ponto não encontrado"));
                existente.setPontos(ponto);
            }

            return new RotaColaboradorResponse(
                    rota.getIdRota(),
                    rota.getNome(),
                    existente.getPontos() != null ? existente.getPontos().getNome() : null,
                    user.getIdColaborador(),
                    user.getNome(),
                    user.getMatricula(),
                    existente.getDataUso(),
                    existente.getPontos() != null ? existente.getPontos().getIdPonto() : null
            );
        }

        var rc = new RotaColaborador();
        rc.setId(new RotaColaboradorId(idColaborador, idRota));
        rc.setRota(rota);
        rc.setColaborador(user);
        rc.setAtivo(true);

        if (idPonto != null) {
            var ponto = pontosRepo.findById(idPonto)
                    .orElseThrow(() -> new NoSuchElementException("Ponto não encontrado"));
            rc.setPontos(ponto);
        }

        rotaColabRepo.save(rc);

        return new RotaColaboradorResponse(
                rota.getIdRota(),
                rota.getNome(),
                rc.getPontos() != null ? rc.getPontos().getNome() : null,
                user.getIdColaborador(),
                user.getNome(),
                user.getMatricula(),
                rc.getDataUso(),
                rc.getPontos() != null ? rc.getPontos().getIdPonto() : null
        );
    }

    @Transactional(readOnly = true)
    public List<RotaColaboradorResponse> listarPorRota(Integer idRota) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new NoSuchElementException("Rota não encontrada"));

        return rotaColabRepo.findByRota_IdRotaAndAtivoTrue(idRota).stream()
                .map(rc -> new RotaColaboradorResponse(
                        rota.getIdRota(),
                        rota.getNome(),
                        rc.getPontos() != null ? rc.getPontos().getNome() : null,
                        rc.getColaborador().getIdColaborador(),
                        rc.getColaborador().getNome(),
                        rc.getColaborador().getMatricula(),
                        rc.getDataUso(),
                        rc.getPontos() != null ? rc.getPontos().getIdPonto() : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RotaColaboradorResponse> listarPorColaborador(UUID idColaborador) {
        var user = userRepo.findById(idColaborador)
                .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));

        return rotaColabRepo.findByColaborador_IdColaboradorAndAtivoTrue(idColaborador).stream()
                .map(rc -> new RotaColaboradorResponse(
                        rc.getRota().getIdRota(),
                        rc.getRota().getNome(),
                        rc.getPontos() != null ? rc.getPontos().getNome() : null,
                        user.getIdColaborador(),
                        user.getNome(),
                        user.getMatricula(),
                        rc.getDataUso(),
                        rc.getPontos() != null ? rc.getPontos().getIdPonto() : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<RotaColaboradorResponse> buscarAtualPorColaborador(UUID idColaborador) {
        var user = userRepo.findById(idColaborador)
                .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));

        return rotaColabRepo
                .findFirstByColaborador_IdColaboradorAndAtivoTrueOrderByDataUsoDesc(idColaborador)
                .map(rc -> new RotaColaboradorResponse(
                        rc.getRota().getIdRota(),
                        rc.getRota().getNome(),
                        rc.getPontos() != null ? rc.getPontos().getNome() : null,
                        user.getIdColaborador(),
                        user.getNome(),
                        user.getMatricula(),
                        rc.getDataUso(),
                        rc.getPontos() != null ? rc.getPontos().getIdPonto() : null
                ));
    }

    @Transactional
    public void remover(Integer idRota, UUID idColaborador) {
        var id = new RotaColaboradorId(idColaborador, idRota);

        var rotaColab = rotaColabRepo.findById(id)
                .orElseThrow(() -> new RegraNegocioException(
                        "VINCULO_INEXISTENTE",
                        "Esse colaborador não está atribuído a essa rota."
                ));

        rotaColab.setAtivo(false);
    }

    public List<MapaColabPontoDTO> mapaColaboradores(Integer idRota) {
        return rotaColabRepo.mapaColaboradoresPorPonto(idRota);
    }
}
