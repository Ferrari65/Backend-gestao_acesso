package com.repositories.registroEmbarque;

import com.domain.user.viagemRota.ViagemRota;
import com.domain.user.colaborador.User;
import com.domain.user.registroEmbarque.RegistroEmbarque;
import com.projection.RotaComMaisEmbarquesHojeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface RegistroEmbarqueRepository extends JpaRepository<RegistroEmbarque, UUID> , JpaSpecificationExecutor<RegistroEmbarque> {
    boolean existsByViagemAndColaborador (ViagemRota viagem, User colaborador);
    Optional<RegistroEmbarque> findByViagemAndColaborador (ViagemRota viagem, User colaborador);

    @Query(value = """
    SELECT 
        r.nome AS nomeRota,
        COUNT(*) AS totalEmbarques
    FROM registro_embarque re
    JOIN viagens_rota v ON v.id_viagem = re.id_viagem
    JOIN rotas r ON r.id_rota = v.id_rota
    WHERE re.data_embarque::date = :data
    GROUP BY r.id_rota, r.nome
    ORDER BY totalEmbarques DESC
    LIMIT 1
    """, nativeQuery = true)
    RotaComMaisEmbarquesHojeProjection findRotaComMaisEmbarquesNaData(@Param("data") LocalDate data);
}
