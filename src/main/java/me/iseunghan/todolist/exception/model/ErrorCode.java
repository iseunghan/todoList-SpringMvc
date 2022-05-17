package me.iseunghan.todolist.exception.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_TODO("TODO doesn't exist"),

    NOT_EMPTY_TITLE("title doesn't blank"),

    ACCOUNT_NOT_FOUND("Account Not Found"),

    UNAUTHORIZED("Unauthorized Error"),

    ACCESS_DENIED("AccessDenied Error"),

    DUPLICATE_ACCOUNT("Duplicated Account!");

    public final String message;
}
