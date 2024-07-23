package com.server.scapture.oauth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey = "z4FmaD1QnM2Fp1XnT6D2O2h1Q2D3P4R5S6T7U8V9W0X1Y2Z3a4b5c6d7e8f9g0h1";

    private SecretKey getSigningKey() {
        // Base64 URL Decoding을 사용하여 키를 디코딩합니다.
        byte[] keyBytes = Decoders.BASE64URL.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final long EXPIRATION_TIME = 86400000; // 1일

    // 토큰 생성 (provider, providerId 사용)
    public String createToken(String provider, String providerId) {
        return Jwts.builder()
                .setSubject(providerId)
                .claim("provider", provider)
                .claim("providerId", providerId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    // 응답 헤더에서 토큰을 반환하는 메서드
    public String getTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        log.warn("Authorization 헤더가 유효하지 않습니다.");
        return null;
    }

    // 토큰에서 클레임을 추출하는 메서드
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 토큰입니다.");
            return null;
        }
    }

    // JWT 토큰에서 provider 추출
    public String getProviderFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.get("provider", String.class);
        }
        return null;
    }

    // JWT 토큰에서 providerId 추출
    public String getProviderIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.get("providerId", String.class);
        }
        return null;
    }

    // Jwt 토큰의 유효기간을 확인하는 메서드
    public Boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        }
        return null;
    }

}