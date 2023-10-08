package com.example.jwt2.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {

    @Value("${com.example.jwt.secret}")
    private String secretKey;

    // JWT 문자열(토큰)을 생성
    public String generateToken(Map<String, Object> valueMap, int days) {
        log.info("Generate Key: " + secretKey);

        // HEADER
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        // PAYLOAD
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);
        // 토큰 유효 시간 설정
        int time = 60 * 24 * days;

        String jwtStr = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return jwtStr;
    }

    // JWT 문자열(토큰)을 검증
    public Map<String, Object> validateToken(String token) throws JwtException {
        Map<String, Object> claim = null;

        claim = Jwts.parser()
                // set Key
                .setSigningKey(secretKey.getBytes())
                // 파싱 및 검증하여 실패시 에러 발생
                .parseClaimsJws(token)
                .getBody();

        return claim;
    }

    // 클레임(페이로드)에서 로그인한 유저 아이디 추출
    public String getUserIdFromToken(String token) {
        try {
            Map<String, Object> claims = validateToken(token);
            return (String) claims.get("mid");
        } catch (JwtException e) {
            log.error("Failed to extract user ID from token", e);
            return null;
        }
    }
    // 클레임(페이로드)에서 로그인한 유저 닉네임 추출
    public String getUserNicknameFromToken(String token) {
        try {
            Map<String, Object> claims = validateToken(token);
            return (String) claims.get("nickname");
        } catch (JwtException e) {
            log.error("Failed to extract user Nickname from token", e);
            return null;
        }
    }

    // 클레임(페이로드)에서 로그인한 유저 권한 추출
    public String getUserRoleFromToken(String token) {
        try {
            Map<String, Object> claims = validateToken(token);
            return (String) claims.get("role");
        } catch (JwtException e) {
            log.error("Failed to extract user Role from token", e);
            return null;
        }
    }
}
