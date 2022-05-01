package me.iseunghan.todolist.exception.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_TODO("TODO doesn't exist"),

    NOT_EMPTY_TITLE("title doesn't blank");

    public final String message;
}
