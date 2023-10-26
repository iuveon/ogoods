package com.ogu.ogoods.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // 스프링 시큐리티 인증 예외 처리 메소드
        // AuthenticationException : 발생한 인증 예외에 대한 정보를 포함하는 객체
        if("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
            /* ajax 요청은 헤더에 x-requested-with 라는 키로 XMLHttpRequest 값을 가지고 있음
            "XMLHttpRequest".equals(request.getHeader("x-requested-with")) : HTTP요청이 AJAX 요청이라면 */
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); // SC_UNAUTHORIZED : 401 ERROR
            // 응답으로 401에러와 함께 Unauthorized 메세지를 전달
        } else {
            response.sendRedirect("/members/login"); // 로그인페이지로 리다이렉트
        }
    }
}
