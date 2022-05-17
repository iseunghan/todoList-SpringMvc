package me.iseunghan.todolist.exception;

import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;

@Getter
public class AccountNotFoundException extends RuntimeException {

    private final String username;

    public AccountNotFoundException(String username) {
        super(ErrorCode.ACCOUNT_NOT_FOUND.message);
        this.username = username;
    }
}
