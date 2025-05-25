package com.example.telegramWebApp.exceptions;

public class TelegramAuthException extends RuntimeException {
  public TelegramAuthException(String message) {
    super(message);
  }
}
