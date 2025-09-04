package com.repositories;

import com.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select u from User u where lower(u.email) = lower(:email)")
    Optional<User> findByEmail(String email);
}
