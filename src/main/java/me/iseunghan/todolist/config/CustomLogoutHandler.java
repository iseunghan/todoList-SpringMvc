package me.iseunghan.todolist.config;

import lombok.extern.slf4j.Slf4j;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.setHeader("Authorization", "");

        authentication = getAuthentication(request);

        log.info("로그아웃 요청으로 사용자의 정보: {} 를 제거합니다.", authentication);
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().equals("Authorization")).findFirst().get();

        String token = cookie.getValue().replace("Bearer", "").trim();
        JwtTokenUtil tokenUtil = new JwtTokenUtil();

        return tokenUtil.getAuthentication(token);
    }
}
