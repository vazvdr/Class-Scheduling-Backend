package com.class_project.backend_class.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.class_project.backend_class.classes.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
