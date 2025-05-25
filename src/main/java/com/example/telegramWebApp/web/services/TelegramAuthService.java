package com.example.telegramWebApp.web.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelegramAuthService {
  @Value("${telegram.bot.token}")
  private String botToken;

  public boolean validateTelegramData(String initData) {
    try {
      Map<String, String> parsedData = parseInitData(initData);
      String receivedHash = parsedData.remove("hash");

      String dataCheckString = buildDataCheckString(parsedData);
      String secretKey =
          new HmacUtils(HmacAlgorithms.HMAC_SHA_256, "WebAppData".getBytes()).hmacHex(botToken);
      String calculatedHash =
          new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey.getBytes()).hmacHex(dataCheckString);

      if (!calculatedHash.equals(receivedHash)) {
        return false;
      }

      if (parsedData.containsKey("auth_date")) {
        long authDate = Long.parseLong(parsedData.get("auth_date"));
        long currentTime = Instant.now().getEpochSecond();
        return currentTime - authDate <= 86400;
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Map<String, String> parseInitData(String initData) {
    Map<String, String> result = new HashMap<>();
    String[] pairs = initData.split("&");
    for (String pair : pairs) {
      String[] keyValue = pair.split("=");
      if (keyValue.length == 2) {
        result.put(keyValue[0], keyValue[1]);
      }
    }
    return result;
  }

  private String buildDataCheckString(Map<String, String> data) {
    return data.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .reduce((a, b) -> a + "\n" + b)
        .orElse("");
  }

  public Map<String, String> extractUserData(String initData) {
    Map<String, String> parsedData = parseInitData(initData);
    Map<String, String> userData = new HashMap<>();

    if (parsedData.containsKey("user")) {
      String userJson = StringUtils.substringBetween(parsedData.get("user"), "{", "}");
      String[] userFields = userJson.split(",");
      for (String field : userFields) {
        String[] keyValue = field.split(":");
        if (keyValue.length == 2) {
          String key = keyValue[0].replace("\"", "").trim();
          String value = keyValue[1].replace("\"", "").trim();
          userData.put(key, value);
        }
      }
    }

    return userData;
  }
}
