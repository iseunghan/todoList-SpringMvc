package me.iseunghan.todolist.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import me.iseunghan.todolist.exception.AccessDeniedException;
import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.AccountAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenUtil {

    @Value("${jwt.config.secret}")
    private String secret;

    @Value("${jwt.config.expiration}")
    private long expiration;

    @Value("${jwt.config.auth_header}")
    private String AUTH_HEADER;

    @Value("${jwt.config.auth_type}")
    private String AUTH_TYPE;

    public String createToken(Authentication authentication) {
        AccountAdapter accountAdapter = (AccountAdapter) authentication.getPrincipal();
        Account account = accountAdapter.getAccount();

        String authorities = accountAdapter.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date expireTime = new Date(System.currentTimeMillis() + this.expiration);

        return JWT.create()
                .withSubject(account.getUsername())
                .withExpiresAt(expireTime)
                .withClaim("authorities", authorities)
                .sign(Algorithm.HMAC512(secret));
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token);
            return true;
        } catch (AlgorithmMismatchException e) {
            log.error("잘못된 알고리즘 형식입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (JWTVerificationException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (RuntimeException e) {
            log.error("잘못된 JWT 토큰입니다.");
        }
        return false;
    }

    public String extractToken(HttpServletRequest request) {
        // find in Header
        String header = request.getHeader(AUTH_HEADER);

        if (StringUtils.hasText(header) && header.startsWith(AUTH_TYPE)) {
            return header.replace(AUTH_TYPE, "").trim();
        }

        // find in Cookie
        Cookie cookie = null;
        if (request.getCookies() != null) {
            cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals(AUTH_HEADER))
                    .findFirst()
                    .orElse(null);
        }

        return (cookie != null) ? cookie.getValue().replace(AUTH_TYPE, "").trim() : null;
    }

    // 이미 AuthorizationFilter를 거쳤기 때문에 따로 null 체크는 하지 않는다.
    public String getUsernameFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);

        Collection<? extends SimpleGrantedAuthority> authorities = Arrays.stream(JWT.decode(token).getClaim("authorities").asString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, token, authorities);
    }

    public boolean isCorrectUsername(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        if (!tokenUsername.equals(username)) {
            throw new AccessDeniedException(username);
        }

        return true;
    }
}
