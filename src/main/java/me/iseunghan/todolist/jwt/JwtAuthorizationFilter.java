package me.iseunghan.todolist.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthorizationFilter(JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenUtil.extractToken(request);

        // TODO token valid
        if (token != null && jwtTokenUtil.validateToken(token)) {
            Authentication authentication = jwtTokenUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Security Context에 사용자 정보: {}를 담았습니다. URI: {}", authentication.toString(), request.getRequestURI());
        }else {
            log.info("유효한 JWT 토큰이 존재하지 않습니다. URI: {}", request.getRequestURI());
        }

        doFilter(request, response, chain);
    }

}
