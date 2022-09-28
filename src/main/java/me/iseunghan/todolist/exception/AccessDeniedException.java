package me.iseunghan.todolist.exception;

import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedException extends GeneralException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(HttpStatus.FORBIDDEN, errorCode);
    }
}
