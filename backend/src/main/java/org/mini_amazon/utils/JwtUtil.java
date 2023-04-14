package org.mini_amazon.utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

  private static final String SECRET_KEY = "asdfdsadaasasdadadadaasdfgasdsadaasasdadadada1234567890gasdsadaasasdadadadaasdfgasdsadaasasdadadada1234567890dadaadadsadaasasdadadadaasdfgasdsadaasasdadadada1234567890shjkl";

  public static String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", username);
    claims.put("created", new Date());
    return Jwts
      .builder()
      .setClaims(claims)
      .setExpiration(new Date(System.currentTimeMillis() + 86400000))
      .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
      .compact();
  }

  public static Claims getClaimsFromToken(String token) {
    return Jwts
      .parser()
      .setSigningKey(SECRET_KEY)
      .parseClaimsJws(token)
      .getBody();
  }
}
