package me.iseunghan.todolist.exception;


import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final String message;

    public GeneralException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
