//package org.mini_amazon.utils;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import javax.crypto.SecretKey;
//import org.mini_amazon.models.User;
//import org.springframework.http.ResponseCookie;
//
//public class JwtTokenUtil {
//
//  private static final String SECRET_KEY =
//    "uihauhisduhaihdiquhwiehiuhihakjsdbkjabdkjabdiqhwuehqihdbksbakjdbakbdkjadkjahodhoqhwoehqdhiajbdksabdkbadhoqhwioqhdakjbdkjsabdka";
//  private static final long EXPIRATION_TIME = 86400000; // 24 hours
//
//  private static final SecretKey key = Keys.hmacShaKeyFor(
//    SECRET_KEY.getBytes()
//  );
//
//  public static String generateToken(Map<String, Object> claims) {
//    return Jwts
//      .builder()
//      .setClaims(claims)
//      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//      .signWith(key, SignatureAlgorithm.HS256)
//      .compact();
//  }
//
//  //   public static Map<String, Object> getClaimsFromToken(String token) {
//  //       try {
//  //           Claims claims = Jwts
//  //                   .parser()
//  //                   .setSigningKey(SECRET_KEY)
//  //                   .parseClaimsJws(token)
//  //                   .getBody();
//  //           return claims;
//  //       } catch (Exception e) {
//  //           return null;
//  //       }
//  //   }
//  public static User getClaimsFromToken(String token) {
//    try {
//      Claims claims = Jwts
//        .parser()
//        .setSigningKey(key)
//        .parseClaimsJws(token)
//        .getBody();
//      ObjectMapper mapper = new ObjectMapper();
//      User user = mapper.convertValue(claims.get("user"), User.class);
//      return user;
//    } catch (Exception e) {
//      return null;
//    }
//  }
//}
