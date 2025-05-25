package com.example.telegramWebApp.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramWebAppBot extends TelegramLongPollingBot {
  @Override
  public String getBotUsername() {
    return "";
  }

  @Override
  public void onRegister() {
    super.onRegister();
  }

  @Override
  public void onUpdateReceived(Update update) {}
}
