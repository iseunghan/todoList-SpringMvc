package me.iseunghan.todolist.common;

import me.iseunghan.todolist.exception.AccessDeniedException;
import me.iseunghan.todolist.exception.model.ErrorCode;

public class AuthUtils {

    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_TYPE = "Bearer ";

    /**
     * 요청한 리소스(username)와 로그인한 사용자(username)이 일치하는지 검증
     * @param username
     * @param loginUsername
     */
    public static void validationUsername(String username, String loginUsername) {
        if (!username.equals(loginUsername)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
        }
    }
}
