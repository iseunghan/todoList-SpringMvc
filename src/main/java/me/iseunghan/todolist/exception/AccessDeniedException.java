package me.iseunghan.todolist.exception;

import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;

@Getter
public class AccessDeniedException extends RuntimeException {
    private final String username;

    public AccessDeniedException(String username) {
        super(ErrorCode.ACCESS_DENIED.message);
        this.username = username;
    }
}
