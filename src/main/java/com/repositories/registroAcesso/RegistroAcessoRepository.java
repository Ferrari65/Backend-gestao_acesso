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

    @Query(value = """
    SELECT * FROM registro_acesso
     WHERE id_pessoa = :idPessoa
       AND tipo_pessoa = CAST(:tipoPessoa AS tipo_pessoa)
       AND saida IS NULL
     LIMIT 1
""", nativeQuery = true)
    Optional<RegistroAcesso> findAbertoDoCondutor(UUID idPessoa, String tipoPessoa);

    @Query("select r from RegistroAcesso r where r.saida is null order by r.entrada desc")
    List<RegistroAcesso> findAbertos();

    @Query("""
      select r from RegistroAcesso r
       where r.entrada >= :de and r.entrada <= :ate
         and (:codPortaria is null or r.codPortaria = :codPortaria)
       order by r.entrada desc
    """)
    List<RegistroAcesso> findHistorico(OffsetDateTime de, OffsetDateTime ate, Integer codPortaria);
}