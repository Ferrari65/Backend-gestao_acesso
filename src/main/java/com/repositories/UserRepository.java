package com.repositories;

import com.domain.user.colaborador.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select u from User u where lower(u.email) = lower(:email)")
    Optional<User> findByEmail(@Param("email") String email);
    Optional<User> findByMatricula(String matricula);

    @EntityGraph(attributePaths = {"cidade", "role"})
    @Query("select u from User u")
    List<User> findAllWithCidadeAndRole();

}
