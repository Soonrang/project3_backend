package com.example.jwt2.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Log4j2
public class JWTUtilTests {
    @Autowired
    private JWTUtil jwtUtil;

    @Test
    public void testGenerate() {
        Map<String, Object> claimMap = Map.of("mid", "ABCDE");
        String jwtStr = jwtUtil.generateToken(claimMap, 1);
        log.info("JWT Token: " + jwtStr);
    }

    // 토큰 유효성 검사
    @Test
    public void testValidate() {
        String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTQzMzE4NjIsIm1pZCI6IkFCQ0RFIiwiaWF0IjoxNjk0MzMxODAyfQ.4CDQ0SR8XJkdkoxs3_Fg9WPqq_eqS71g55uerZ4ip0kaa";
        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);
        log.info(claim);
    }

    @Test
    public void testAll() {
        String jwtStr = jwtUtil.generateToken(Map.of("mid", "AAAA", "email", "aaaa@bbb.com"), 1);
        log.info("JWT String:" + jwtStr);

        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);
        log.info("MID:" + claim.get("mid"));
        log.info("Email:" + claim.get("email"));
    }
}
