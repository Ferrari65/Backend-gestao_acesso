package com.services.colaborador;

import com.dto.colaborador.RotaColaboradorResponse;
import com.domain.user.Rotas.RotaColaborador;
import com.domain.user.Rotas.RotaColaboradorId;
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

        Boolean ativo = (user.getAtivo());
        if (ativo == null || !ativo) {
            throw new RegraNegocioException("COLABORADOR_INATIVO", "Colaborador precisa estar ativo.");
        }

        boolean jaEmOutraRota = rotaColabRepo
                .existsByColaborador_IdColaboradorAndId_IdRotaNot(idColaborador, idRota);
        if (jaEmOutraRota) {
            throw new RegraNegocioException("COLABORADOR_JA_ATRIBUIDO",
                    "Colaborador já está atribuído a outra rota.");
        }

        var existente = rotaColabRepo.findById_IdRotaAndId_IdColaborador(idRota, idColaborador).orElse(null);
        if (existente != null) {
            if (idPonto != null) {
                var ponto = pontosRepo.findById(idPonto)
                        .orElseThrow(() -> new NoSuchElementException("Ponto não encontrado"));
                existente.setPontos(ponto); // dirty checking
            }
            return new RotaColaboradorResponse(
                    idRota, idColaborador, user.getNome(),
                    existente.getDataUso(),
                    existente.getPontos() != null ? existente.getPontos().getIdPonto() : null
            );
        }

        var rc = new RotaColaborador();
        rc.setId(new RotaColaboradorId(idColaborador, idRota));
        rc.setRota(rota);
        rc.setColaborador(user);
        if (idPonto != null) {
            var ponto = pontosRepo.findById(idPonto)
                    .orElseThrow(() -> new NoSuchElementException("Ponto não encontrado"));
            rc.setPontos(ponto);
        }
        rotaColabRepo.save(rc);

        return new RotaColaboradorResponse(
                idRota, idColaborador, user.getNome(),
                rc.getDataUso(),
                rc.getPontos() != null ? rc.getPontos().getIdPonto() : null
        );
    }

    @Transactional(readOnly = true)
    public List<RotaColaboradorResponse> listarPorRota(Integer idRota) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new NoSuchElementException("Rota não encontrada"));

        return rotaColabRepo.findByRota_IdRota(idRota).stream()
                .map(rc -> new RotaColaboradorResponse(
                        rota.getIdRota(),
                        rc.getColaborador().getIdColaborador(),
                        rc.getColaborador().getNome(),
                        rc.getDataUso(),
                        rc.getPontos() != null ? rc.getPontos().getIdPonto() : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RotaColaboradorResponse> listarPorColaborador(UUID idColaborador) {
        var user = userRepo.findById(idColaborador)
                .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));

        return rotaColabRepo.findByColaborador_IdColaborador(idColaborador).stream()
                .map(rc -> new RotaColaboradorResponse(
                        rc.getRota().getIdRota(),
                        user.getIdColaborador(),
                        user.getNome(),
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
                .findFirstByColaborador_IdColaboradorOrderByDataUsoDesc(idColaborador)
                .map(rc -> new RotaColaboradorResponse(
                        rc.getRota().getIdRota(),
                        user.getIdColaborador(),
                        user.getNome(),
                        rc.getDataUso(),
                        rc.getPontos() != null ? rc.getPontos().getIdPonto() : null
                ));
    }

    @Transactional
    public void remover(Integer idRota, UUID idColaborador) {
        if (rotaColabRepo.existsByColaborador_IdColaboradorAndRota_IdRota(idColaborador, idRota)) {
            rotaColabRepo.deleteById_IdRotaAndId_IdColaborador(idRota, idColaborador);
        }
    }
}
