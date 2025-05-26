package com.example.telegramWebApp.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class ParserUtil {
  public Map<String, String> parseInitData(String initData) {
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

  public String buildDataCheckString(Map<String, String> data) {
    return data.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .reduce((a, b) -> a + "\n" + b)
        .orElse("");
  }

  public Map<String, String> extractUserData(String initData) {
    Map<String, String> parsedData = ParserUtil.parseInitData(initData);
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
