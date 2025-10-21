package com.repositories.registroAcesso;

import com.domain.user.Enum.TipoPessoa;
import com.domain.user.registroAcesso.RegistroAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroAcessoRepository extends JpaRepository<RegistroAcesso, UUID> {

    List<RegistroAcesso> findByEntradaGreaterThanEqualAndEntradaLessThanOrderByEntradaDesc(
            OffsetDateTime inicio,
            OffsetDateTime fimExclusivo
    );

    Optional<RegistroAcesso> findById(UUID id);

    Optional<RegistroAcesso> findByIdAndSaidaIsNull(UUID id);

    @Query("SELECT r FROM RegistroAcesso r WHERE r.idPessoa = :idPessoa AND r.tipoPessoa = :tipoPessoa AND r.saida IS NULL")
    Optional<RegistroAcesso> findAbertoDoCondutor(UUID idPessoa, TipoPessoa tipoPessoa);

    @Query("SELECT r FROM RegistroAcesso r WHERE r.saida IS NULL")
    List<RegistroAcesso> findAbertos();

    List<RegistroAcesso> findByCodPortariaOrderByEntradaDesc(Short codPortaria);
    List<RegistroAcesso> findByTipoPessoaOrderByEntradaDesc(TipoPessoa tipoPessoa);
}