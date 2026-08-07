package com.cashlens.expensetracker.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cashlens.expensetracker.models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findByPasswordResetToken(String token);

}