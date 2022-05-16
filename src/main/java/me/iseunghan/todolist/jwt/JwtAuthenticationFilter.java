package me.iseunghan.todolist.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.iseunghan.todolist.model.dto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${jwt.config.auth_header}")
    private String AUTH_HEADER;
    @Value("${jwt.config.auth_type}")
    private String AUTH_TYPE;

    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super.setAuthenticationManager(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // parse username, password
        String[] userInfo = getUsername_and_Password(request);
        String username = userInfo[0];
        String password = userInfo[1];

        log.info("[Login by Username] username:{}",username);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(token);
    }

    private String[] getUsername_and_Password(HttpServletRequest request) {
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            try {
                LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
                username = (loginDto.getUsername() != null) ? loginDto.getUsername() : "";
                password = (loginDto.getPassword() != null) ? loginDto.getPassword() : "";

            } catch (IOException e) {
                log.error("UserInfo Parse ERROR");
                username = "";
                password = "";
            }
        }
        return new String[]{username, password};
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String token = jwtTokenUtil.createToken(authentication);
        System.out.println("TOKEN create: " + token);
        response.setHeader(AUTH_HEADER, AUTH_TYPE + " " + token);

        Cookie cookie = new Cookie(AUTH_HEADER, AUTH_TYPE + token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");

        System.out.println(AUTH_HEADER + ", " + AUTH_TYPE);
        System.out.println(cookie);
        response.addCookie(cookie);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
        log.error("유저 정보가 일치하지 않습니다. USER: {}", failed.getMessage());
    }
}
