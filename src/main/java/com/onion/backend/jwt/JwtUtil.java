package com.onion.backend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Secret key를 환경 변수 또는 설정 파일에서 주입 받음
    @Value("${jwt.secret}")
    private String secret;

    // 토큰의 유효 시간 설정 (예: 10시간)
    @Value("${jwt.tokenValidity}")
    private long tokenValidity;

    // JWT 토큰에서 사용자 이름을 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 토큰의 만료일을 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Claims에서 특정 값을 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 Claims를 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // JWT 토큰 생성
    public String generateToken(String username) {
        return createToken(username);
    }

    // 토큰 생성
    private String createToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰이 유효한지 확인 (사용자 이름과 만료 시간 체크)
    public Boolean validateToken(String token) {
        //추후 데이터베이스 연동하여 유저 찾는 작업 진행
        final String extractedUsername = extractUsername(token);
        return !isTokenExpired(token);
    }

    // 서명에 사용할 Key 생성
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}