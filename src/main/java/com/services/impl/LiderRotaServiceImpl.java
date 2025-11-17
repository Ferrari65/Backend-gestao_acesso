package com.services.impl;

import com.dto.liderRota.LiderRotaResponse;
import com.domain.user.LiderRota.LiderRotaId;
import com.domain.user.LiderRota.RotaLider;
import com.exceptions.RegraNegocioException;
import com.repositories.Rota.RotaColaboradorRepository;
import com.repositories.Rota.RotaPontoRepository;
import com.repositories.Rota.RotaRepository;
import com.repositories.UserRepository;
import com.repositories.liderRota.LiderRotaRepository;
import com.services.liderRota.LiderRotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiderRotaServiceImpl implements LiderRotaService {

    private final LiderRotaRepository liderRotaRepo;
    private final RotaRepository rotaRepo;
    private final UserRepository userRepo;
    private final com.repositories.RoleRepository roleRepo;
    private final RotaColaboradorRepository rotaColabRepo;
    private final RotaPontoRepository rotaPontoRepo;

    @Transactional
    @Override
    public LiderRotaResponse atribuir(Integer idRota, UUID idColaborador) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new NoSuchElementException("Rota não encontrada"));

        var user = userRepo.findById(idColaborador)
                .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));

        var rc = rotaColabRepo.findById_IdRotaAndId_IdColaborador(idRota, idColaborador)
                .orElseThrow(() -> new RegraNegocioException("Colaborador não está atribuído a esta rota."));

        if (rc.getPontos() == null) {
            throw new RegraNegocioException("Para ser líder, o colaborador precisa ter um ponto definido na rota.");
        }

        Integer idPonto = rc.getPontos().getIdPonto();

        boolean ePrimeiro = rotaPontoRepo
                .existsByRota_IdRotaAndPonto_IdPontoAndOrdem(idRota, idPonto, 1);
        if (!ePrimeiro) {
            throw new RegraNegocioException("Apenas colaboradores no ponto de ORDEM 1 podem ser líderes dessa rota.");
        }

        var existenteOpt = liderRotaRepo.findByRota_IdRotaAndColaborador_IdColaborador(idRota, idColaborador);
        LocalDateTime quando;

        if (existenteOpt.isPresent()) {
            var existente = existenteOpt.get();
            if (!existente.isAtivo()) {
                existente.setAtivo(true);
                existente.setDataInativacao(null);
                if (existente.getDataAtribuicao() == null) {
                    existente.setDataAtribuicao(LocalDateTime.now());
                }
                liderRotaRepo.save(existente);
            }
            quando = existente.getDataAtribuicao();
        } else {
            var novo = new RotaLider();
            // Usa a mesma ordem do seu construtor: (UUID idColaborador, Integer idRota)
            novo.setId(new LiderRotaId(idColaborador, idRota));
            novo.setRota(rota);
            novo.setColaborador(user);
            novo.setAtivo(true);
            novo.setDataAtribuicao(LocalDateTime.now());
            novo.setDataInativacao(null);
            liderRotaRepo.save(novo);
            quando = novo.getDataAtribuicao();
        }

        var roleLider = roleRepo.findByNome("LIDER")
                .orElseThrow(() -> new NoSuchElementException("Role LIDER não encontrada"));
        user.setRole(roleLider);
        userRepo.save(user);

        return new LiderRotaResponse(
                rota.getIdRota(),
                user.getIdColaborador(),
                user.getNome(),
                quando
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<LiderRotaResponse> buscarTodos(Integer idRota) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new NoSuchElementException("Rota não encontrada"));

        var ativos = liderRotaRepo.findByRota_IdRotaAndAtivoTrue(idRota);
        var resp = new ArrayList<LiderRotaResponse>(ativos.size());

        for (var lr : ativos) {
            var user = lr.getColaborador();
            resp.add(new LiderRotaResponse(
                    rota.getIdRota(),
                    user.getIdColaborador(),
                    user.getNome(),
                    lr.getDataAtribuicao()
            ));
        }
        return resp;
    }

    @Transactional
    @Override
    public void remover(Integer idRota, UUID idColaborador) {
        var lr = liderRotaRepo.findByRota_IdRotaAndColaborador_IdColaborador(idRota, idColaborador)
                .orElseThrow(() -> new NoSuchElementException("Liderança não encontrada"));

        if (lr.isAtivo()) {
            lr.setAtivo(false);
            lr.setDataInativacao(LocalDateTime.now());
            liderRotaRepo.save(lr);
        }

        boolean aindaLider = liderRotaRepo.existsByColaborador_IdColaboradorAndAtivoTrue(idColaborador);
        if (!aindaLider) {
            var user = userRepo.findById(idColaborador)
                    .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));

            var roleColab = roleRepo.findByNome("COLABORADOR")
                    .orElseThrow(() -> new NoSuchElementException("Role COLABORADOR não encontrada"));

            user.setRole(roleColab);
            userRepo.save(user);
        }
    }
}
