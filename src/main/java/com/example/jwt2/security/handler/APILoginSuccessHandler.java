package com.example.jwt2.security.handler;

import com.example.jwt2.dto.apiuser.APIUserDTO;
import com.example.jwt2.util.JWTUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    // 인증 성공 후, 후 처리 작업을 담당하는 핸들러
    // 인증된 사용자에게 Access Token / Refresh Token을 발행
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login Success Handler Executed.......");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info("Authentication:" + authentication);
        log.info("Authentication Name:" + authentication.getName());

        // 이 방식을 통해 아이디에 해당하는 닉네임을 가져온다
        APIUserDTO apiUserDTO = (APIUserDTO) authentication.getPrincipal();
        //log.info(apiUserDTO.getNickname());
        //log.info(apiUserDTO.getRole());

        // username, nickname 정보가 담긴 토큰 생성
        Map<String, Object> claim = Map.of("mid", authentication.getName(), "nickname", apiUserDTO.getNickname(), "role", apiUserDTO.getRole());
        // Access Token 유효기간 1일
        String accessToken = jwtUtil.generateToken(claim, 1);
        // refresh Token 유효기간 30일
        String refreshToken = jwtUtil.generateToken(claim, 30);

        Gson gson = new Gson();

        Map<String, String> keyMap = Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        String jsonStr = gson.toJson(keyMap);
        // 이렇게 데이터를 보내면 해당 데이터는 응답 본문에 포함된다
        response.getWriter().println(jsonStr);
    }
}
