package com.repositories;

import com.domain.user.colaborador.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    @EntityGraph(attributePaths = {"cidade", "role"})
    Optional<User> findById(UUID id);

    interface Resumo {
        String getNome();
        String getMatricula();
    }

    @Query("select u.nome as nome, u.matricula as matricula from User u where u.id = :id")
    Optional<Resumo> findResumoByIdColaborador(@Param("id") UUID id);

    @Query("select u from User u where lower(u.email) = lower(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByMatricula(String matricula);

    List<User> findByMatriculaIn(Collection<String> matriculas);

    @EntityGraph(attributePaths = {"cidade", "role"})
    @Query("select u from User u")
    List<User> findAllWithCidadeAndRole();
}
