package com.server.scapture.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "your-secret-key";
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    // JWT 토큰 생성 메서드
    public static String createToken(String provider, String providerId) {
        return Jwts.builder()
                .setSubject(providerId)
                .claim("provider", provider)
                .claim("providerId", providerId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // JWT 토큰에서 클레임 추출 메서드
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 토큰에서 provider 추출
    public static String getProviderFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("provider", String.class);
    }

    // JWT 토큰에서 providerId 추출
    public static String getProviderIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("providerId", String.class);
    }
}
