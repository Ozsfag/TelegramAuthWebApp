package com.example.telegramWebApp.services;

import com.example.telegramWebApp.entities.User;
import com.example.telegramWebApp.mappers.UserMapper;
import com.example.telegramWebApp.utils.ParserUtil;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  @Value("${telegram.bot.token}")
  private String botToken;
  private final UserMapper userMapper;
  private final Key accessTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final Key refreshTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  public User getUserFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(accessTokenKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

    User user = new User();
    user.setUuid(UUID.fromString(claims.get("id", String.class)));
    user.setTelegramId(claims.get("tg_id", Long.class));
    return user;
  }
  public User validateAndGetUser(String initData) {
    if (!validateTelegramData(initData)) return null;
    return userMapper.dataToUser(ParserUtil.parseInitData(initData));
  }

  public String generateAccessToken(User user) {
    return Jwts.builder()
            .claim("id", user.getUuid())
            .claim("tg_id", user.getTelegramId())
            .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // 5 минут
            .signWith(accessTokenKey)
            .compact();
  }

  public String generateRefreshToken(User user) {
    return Jwts.builder()
            .claim("id", user.getUuid())
            .claim("tg_id", user.getTelegramId())
            .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 дней
            .signWith(refreshTokenKey)
            .compact();
  }

  public boolean validateAccessToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

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
