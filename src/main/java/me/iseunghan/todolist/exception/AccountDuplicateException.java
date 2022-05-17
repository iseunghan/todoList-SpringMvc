package me.iseunghan.todolist.exception;

import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;

@Getter
public class AccountDuplicateException extends RuntimeException {

    private final String username;
    public AccountDuplicateException(String username) {
        super(ErrorCode.DUPLICATE_ACCOUNT.message);
        this.username = username;
    }
}
