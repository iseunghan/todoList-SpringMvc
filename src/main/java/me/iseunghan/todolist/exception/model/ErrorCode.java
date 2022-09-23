package me.iseunghan.todolist.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Todo_Error
    NOT_FOUND_TODO("T001", "TODO doesn't exist"),
    NOT_EMPTY_TITLE("T002", "title doesn't blank"),

    // Account_Error
    NOT_FOUND_ACCOUNT("A001", "Account Not Found"),
    DUPLICATE_ACCOUNT("A002", "Duplicated Account!"),

    // Security_Error
    UNAUTHORIZED("S001", "Unauthorized Error"),
    ACCESS_DENIED("S002", "AccessDenied Error"),

    // BadRequest
    FIELD_ERROR("F001", "Check Request Argument")
    ;

    private final String code;
    private final String message;
}
