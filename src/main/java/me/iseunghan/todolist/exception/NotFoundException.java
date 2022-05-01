package me.iseunghan.todolist.exception;

import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;

@Getter
public class NotFoundException extends RuntimeException {

    private final Long id;

    public NotFoundException(Long id) {
        super(ErrorCode.NOT_FOUND_TODO.message);
        this.id = id;
    }
}
