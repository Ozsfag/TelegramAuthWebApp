package com.example.telegramWebApp.repositories;

import com.example.telegramWebApp.entities.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {}
