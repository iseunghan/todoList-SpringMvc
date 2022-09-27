package me.iseunghan.todolist.controller;

import lombok.RequiredArgsConstructor;
import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.common.AuthUtils;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import me.iseunghan.todolist.model.dto.SignInDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static me.iseunghan.todolist.common.AuthUtils.*;

@RequiredArgsConstructor
@RestController
public class SignInController {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> signIn(
            @Valid @RequestBody SignInDto signInDto,
            HttpServletResponse response
    ) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(signInDto.getUsername(), signInDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String jwt = jwtTokenUtil.createToken(authenticate);
        response.addCookie(createCookie(jwt));

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(AUTH_HEADER, AUTH_TYPE + jwt)
                .body(ApiResponse.<Void>of()
                        .success(true)
                        .error(null)
                        .content(null)
                        .build()
                );
    }

    private Cookie createCookie(String token) {
        Cookie cookie = new Cookie(AUTH_HEADER, "Bearer" + token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");

        return cookie;
    }
}
