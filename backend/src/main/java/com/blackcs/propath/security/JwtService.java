package com.blackcs.propath.security;

import com.blackcs.propath.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expirationMs;

  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.expiration-ms}") long expirationMs) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public IssuedToken generateToken(User user) {
    Instant now = Instant.now();
    Instant exp = now.plusMillis(expirationMs);
    String token =
        Jwts.builder()
            .subject(user.getEmail())
            .claim("userId", user.getId())
            .claim("name", user.getName())
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    return new IssuedToken(token, exp.toEpochMilli());
  }

  public Claims parse(String token) throws JwtException {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  public String extractEmail(String token) {
    return parse(token).getSubject();
  }

  public record IssuedToken(String token, long expiresAt) {}
}
