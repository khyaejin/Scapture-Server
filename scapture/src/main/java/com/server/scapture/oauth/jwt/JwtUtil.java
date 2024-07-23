package com.server.scapture.oauth.jwt;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.controller.SignController;
import com.server.scapture.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    private SecretKey getSigningKey() {
        log.info("secretKey : {}",secretKey);

        // Base64 디코딩을 사용하여 키를 디코딩
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey); // BASE64URL이 아닌 BASE64로 변경
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
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // SignatureAlgorithm을 명시적으로 추가
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

    public Optional<User> findUserByJwtToken(String authorizationHeader) {
        String token = getTokenFromHeader(authorizationHeader);
        if (token == null) {
            log.warn("헤더에 JWT 토큰이 없음");
            return null;
        }

        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            log.warn("유효하지 않은 JWT 토큰");
            return null;
        }

        String provider = getProviderFromToken(token);
        String providerId = getProviderIdFromToken(token);

        if (provider == null || providerId == null) {
            log.warn("Provider 또는 providerId 가 JWT에 들어있지 않습니다.");
            return null;
        }

        Optional<User> foundUser =userRepository.findByProviderAndProviderId(provider, providerId);
        if (foundUser.isEmpty()) {
            log.warn("해당 provider, providerId를 가진 회원이 존재하지 않습니다.");
            return null;
        }

        return foundUser;
    }
}
