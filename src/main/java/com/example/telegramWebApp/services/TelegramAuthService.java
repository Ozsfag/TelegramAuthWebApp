package com.example.telegramWebApp.services;

import com.example.telegramWebApp.utils.ParserUtil;
import java.time.Instant;
import java.util.Map;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelegramAuthService {
  @Value("${telegram.bot.token}")
  private String botToken;

  public boolean validateTelegramData(String initData) {
    try {
      Map<String, String> parsedData = ParserUtil.parseInitData(initData);
      String receivedHash = parsedData.remove("hash");

      String dataCheckString = ParserUtil.buildDataCheckString(parsedData);
      String secretKey =
          new HmacUtils(HmacAlgorithms.HMAC_SHA_256, "WebAppData".getBytes()).hmacHex(botToken);
      String calculatedHash =
          new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey.getBytes()).hmacHex(dataCheckString);

      if (!calculatedHash.equals(receivedHash)) {
        return false;
      }

      if (parsedData.containsKey("auth_date")) {
        return isAuthDateLessThenDay(parsedData);
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isAuthDateLessThenDay(Map<String, String> parsedData) {
    long authDate = Long.parseLong(parsedData.get("auth_date"));
    long currentTime = Instant.now().getEpochSecond();
    return currentTime - authDate <= 86400;
  }
}
