package me.iseunghan.todolist.exception;

import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends GeneralException {

    public NotFoundException(ErrorCode errorCode) {
        super(HttpStatus.NOT_FOUND, errorCode);
    }
}
