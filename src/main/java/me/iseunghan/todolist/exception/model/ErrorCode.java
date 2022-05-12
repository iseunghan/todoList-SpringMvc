package me.iseunghan.todolist.exception.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_TODO("TODO doesn't exist"),

    NOT_EMPTY_TITLE("title doesn't blank"),

    USER_NOT_FOUND("User Not Found"),

    UNAUTHORIZED("Unauthorized Error"),

    ACCESS_DENIED("AccessDenied Error");

    public final String message;
}
