package me.iseunghan.todolist.exception;

import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;

@Getter
public class NotEmptyException extends RuntimeException {

    private final Long id;

    public NotEmptyException(Long id) {
        super(ErrorCode.NOT_EMPTY_TITLE.message);
        this.id = id;
    }
}
