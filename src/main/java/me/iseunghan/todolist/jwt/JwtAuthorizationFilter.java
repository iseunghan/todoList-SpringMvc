package me.iseunghan.todolist.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
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

    @Value("${jwt.config.auth_header}")
    private String AUTH_HEADER;

    @Value("${jwt.config.auth_type}")
    private String AUTH_TYPE;

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthorizationFilter(JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        super(authenticationManager);
//        System.out.println("AUTH_HEADER: "+ AUTH_HEADER);
//        System.out.println("AUTH_TYPE: " + AUTH_TYPE);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = extractToken(request);

        // TODO token valid
        if (token != null && jwtTokenUtil.validateToken(token)) {
            Authentication authentication = jwtTokenUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Security Context에 사용자 정보: {}를 담았습니다. URI: {}", authentication.toString(), request.getRequestURI());
        }else {
            log.info("유효한 JWT 토큰이 존재하지 않습니다. URI: {}", request.getRequestURI());
        }

        // TODO Spring Security Context에 Authenticate 객체 넣기. (role 관리)

        doFilter(request, response, chain);
    }

    private String extractToken(HttpServletRequest request) {
        // find in Header
        String header = request.getHeader(AUTH_HEADER);

        if (StringUtils.hasText(header) && header.startsWith(AUTH_TYPE)) {
            return header.replace(AUTH_TYPE, "").trim();
        }

        // find in Cookie
        Cookie cookie = null;
        if(request.getCookies() != null) {
            cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(AUTH_HEADER))
                    .findFirst()
                    .orElse(null);
        }

        return (cookie != null) ? cookie.getValue().replace(AUTH_TYPE, "").trim() : null;
    }

}
