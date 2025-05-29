package com.example.telegramWebApp.controllers;

import com.example.telegramWebApp.entities.User;
import com.example.telegramWebApp.services.AuthService;
import com.example.telegramWebApp.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    // Главная страница с проверкой авторизации
    @GetMapping("/")
    public String home(
            @CookieValue(name = "ACCESS_TOKEN", required = false) String accessToken,
            Model model
    ) {
        if (accessToken != null && authService.validateAccessToken(accessToken)) {
            User user = authService.getUserFromToken(accessToken);
            model.addAttribute("user", user);
            return "index"; // Шаблон для авторизованных
        }
        return "login"; // Шаблон для гостей
    }

    // Обработка входа через Telegram
    @PostMapping("/signin")
    public String signIn(
            @RequestParam String initData,
            HttpServletResponse response,
            Model model
    ) {
        try {
            User user = authService.validateAndGetUser(initData);
            if (user == null) {
                return "redirect:/auth/?error=invalid_data";
            }

            // Генерация токенов и установка в cookies
            addAuthCookies(response, user);
            model.addAttribute("user", user);
            return "redirect:/auth/"; // Перенаправление на главную

        } catch (Exception e) {
            return "redirect:/auth/?error=auth_failed";
        }
    }

    private void addAuthCookies(HttpServletResponse response, User user) {
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", authService.generateAccessToken(user));
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", authService.generateRefreshToken(user));
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
    }
}