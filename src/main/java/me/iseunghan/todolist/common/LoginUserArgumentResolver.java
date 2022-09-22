package me.iseunghan.todolist.common;

import lombok.RequiredArgsConstructor;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        return getLoginUsername(webRequest.getNativeRequest(HttpServletRequest.class));
    }

    private String getLoginUsername(HttpServletRequest httpServletRequest) {
        String token = jwtTokenUtil.extractToken(httpServletRequest);
        return jwtTokenUtil.getUsernameFromToken(token);
    }
}
