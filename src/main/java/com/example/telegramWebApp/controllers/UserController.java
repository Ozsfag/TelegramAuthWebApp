package com.example.telegramWebApp.controllers;

import com.example.telegramWebApp.entities.User;
import com.example.telegramWebApp.mappers.UserMapper;
import com.example.telegramWebApp.services.TelegramAuthService;
import com.example.telegramWebApp.services.UserService;
import com.example.telegramWebApp.utils.ParserUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://telegramauthwebapp.onrender.com")
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
  private final TelegramAuthService telegramAuthService;
  private final UserMapper userMapper;
  private final UserService userService;

  @GetMapping("/")
  public String home(
      Model model,
      HttpSession session,
      @RequestParam(name = "initData", required = false) @NotNull String initData) {
    if (telegramAuthService.validateTelegramData(initData)) {
      Map<String, String> userData = ParserUtil.extractUserData(initData);
      User currentUser = userMapper.dataToUser(userData);

      userService
          .getUserById(currentUser.getUuid())
          .ifPresentOrElse(
              existingUser -> {
                existingUser = userMapper.updateUser(existingUser, currentUser);
                session.setAttribute("user", existingUser);
              },
              () -> {
                userService.saveUser(currentUser);
                session.setAttribute("user", currentUser);
              });
    }

    User user = (User) session.getAttribute("user");
    model.addAttribute("user", user);
    return "index";
  }
}
