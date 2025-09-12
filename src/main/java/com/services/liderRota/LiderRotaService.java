package com.services.liderRota;

import com.dto.liderRota.LiderRotaResponse;
import com.repositories.RoleRepository;
import com.repositories.Rota.RotaRepository;
import com.repositories.UserRepository;
import com.repositories.liderRota.LiderRotaRepository;
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
public class LiderRotaService {

    private final LiderRotaRepository liderRotaRepo;
    private final RotaRepository rotaRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    @Transactional
    public LiderRotaResponse atribuir(Integer idRota, UUID idColaborador) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new NoSuchElementException("Rota não encontrada"));

        var user = userRepo.findById(idColaborador)
                .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado"));

        liderRotaRepo.addLider(idRota, idColaborador);

        var roleLider = roleRepo.findByNome("LIDER")
                .orElseThrow(() -> new NoSuchElementException("Role LIDER não encontrada"));
        user.setRole(roleLider);
        userRepo.save(user);

        LocalDateTime quando = liderRotaRepo.findDataAtribuicao(idRota, idColaborador);

        return new LiderRotaResponse(
                rota.getIdRota(),
                user.getIdColaborador(),
                user.getNome(),
                quando
        );
    }

    @Transactional(readOnly = true)
    public List<LiderRotaResponse> buscarTodos(Integer idRota) {
        var rota = rotaRepo.findById(idRota)
                .orElseThrow(() -> new NoSuchElementException("Rota não encontrada"));

        var ids = liderRotaRepo.findLideresDaRota(idRota);
        List<LiderRotaResponse> resp = new ArrayList<>(ids.size());

        for (UUID idColab : ids) {
            var user = userRepo.findById(idColab)
                    .orElseThrow(() -> new NoSuchElementException("Colaborador não encontrado: " + idColab));

            var quando = liderRotaRepo.findDataAtribuicao(idRota, idColab);
            resp.add(new LiderRotaResponse(
                    rota.getIdRota(),
                    user.getIdColaborador(),
                    user.getNome(),
                    quando
            ));
        }
        return resp;
    }

    @Transactional
    public void remover(Integer idRota, UUID idColaborador) {

        liderRotaRepo.deleteUmLider(idRota, idColaborador);

        boolean aindaLider = liderRotaRepo.isLiderEmAlgumaRota(idColaborador);
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
