package com.asdf.minilog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serial;
import java.io.Serializable;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil implements Serializable {
    @Serial
    private static final long serialVersionUID = -2803737781367947994L;
    // 유효 시간 설정 (5시간 5시간 * 60분 * 60초)
    public static final long JWT_VALIDITY = 5 * 60 * 60;

    @Value("${JWT_SECRET_KEY}")
    private String secret;

    // 토큰에서 사용자 명 추출
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        String jwt;
        if (token.startsWith("Bearer ")) {
            jwt = token.substring(7); // "Bearer " 접두사 제거
        } else {
            jwt = token; // "Bearer " 접두사 없는 경우
        }

        return getClaimFromToken(jwt, claims -> claims.get("userId", Long.class));
    }

    // 토큰 만료 시간 추출
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰 파싱해 모든 클레임 반환, 서명 키 사용하여 토큰 무결성 확인
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // HS256 알고리즘에 맞는 서명 키 생성
    public Claims getAllClaimsFromToken(String token) {
        Key signingKey =
                new SecretKeySpec(
                        Base64.getDecoder().decode(secret),
                        SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parserBuilder().setSigningKey(signingKey).build().
                parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        // 현재 시간보다 이전이면 만료
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails, Long userId) {
        // Claims은 JWT의 payload에 저장되는 정보를 담는 객체
        Map<String, Object> claims = new HashMap<>();
        // 생성할 떄 넣은 userId 꺼냄
        claims.put("userId", userId);

        // JWT 생성
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY * 1000))
                .signWith(
                        new SecretKeySpec(
                                Base64.getDecoder().decode(secret),
                                SignatureAlgorithm.HS256.getJcaName()))
                .compact();
    }

    // 토큰 검증
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
