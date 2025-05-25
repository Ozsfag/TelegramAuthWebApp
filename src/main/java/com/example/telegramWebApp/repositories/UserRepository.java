package com.example.telegramWebApp.repositories;

import com.example.telegramWebApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {}
