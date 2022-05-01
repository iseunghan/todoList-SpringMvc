package me.iseunghan.todolist.controller.advice;

import me.iseunghan.todolist.exception.NotEmptyException;
import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.exception.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"me.iseunghan.todolist"})
public class GlobalExceptionHandler {

    @ExceptionHandler(NotEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse _400(NotEmptyException e) {
        return new ErrorResponse(e.getId(), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse _404(NotFoundException e) {
        return new ErrorResponse(e.getId(), e.getMessage());
    }
}
