package com.repositories.viagem;

import com.domain.user.viagemRota.ViagemRota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ViagemRepository extends JpaRepository<ViagemRota, UUID> {

    List<ViagemRota> findByIdRota(Integer idRota);
    List<ViagemRota> findByAtivo(boolean ativo);
    List<ViagemRota> findByData(LocalDate data);

    @Query(value = "select v.id_rota from viagens_rota v where v.id_viagem = :idViagem", nativeQuery = true)
    Integer findIdRotaByIdViagem(UUID idViagem);

    @Query(value = "select v.data from viagens_rota v where v.id_viagem = :idViagem", nativeQuery = true)
    LocalDate findDataViagem(UUID idViagem);
}
