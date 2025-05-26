package com.example.telegramWebApp.services;

import com.example.telegramWebApp.entities.User;
import com.example.telegramWebApp.repositories.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;

  public Optional<User> getUserById(UUID uuid) {
    log.info("UserService -> getUserById({})", uuid);
    return userRepository.findById(uuid);
  }

  public User saveUser(User user) {
    log.info("UserService -> saveUser");
    return userRepository.saveAndFlush(user);
  }
}
