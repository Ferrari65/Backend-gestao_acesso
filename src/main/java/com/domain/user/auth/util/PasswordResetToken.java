package com.domain.user.auth.util;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens",
        indexes = {
            @Index(name = "idx_prt_email", columnList = "email"),
            @Index(name = "idx_prt_token_hash", columnList = "token_hash", unique = true)
        })
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private  String email;

        @Column(name = "token_hash", nullable = false, unique = true, length = 64)
        private String tokenHash;

        @Column(nullable = false)
        private Instant expiresAt;

        @Column(nullable = false)
        private Boolean used = false;

        @Column(nullable = false)
        private Instant createdAt = Instant.now();

}
