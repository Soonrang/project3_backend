package com.example.jwt2.security.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 차후 여기서 원하는 예외 처리를 할 수 있습니다.
        //response.sendRedirect("/access-denied"); // 예를 들어, "/access-denied" 페이지로 리다이렉트
        log.info("access denied");
    }
}

