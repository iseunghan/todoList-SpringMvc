package me.iseunghan.todolist.controller.advice;

import me.iseunghan.todolist.exception.AccountDuplicateException;
import me.iseunghan.todolist.exception.NotEmptyException;
import me.iseunghan.todolist.exception.TodoNotFoundException;
import me.iseunghan.todolist.exception.AccountNotFoundException;
import me.iseunghan.todolist.exception.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"me.iseunghan.todolist"})
public class GlobalExceptionHandler {

    @ExceptionHandler(NotEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse _todo_400(NotEmptyException e) {
        return new ErrorResponse(e.getId(), e.getMessage());
    }

    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse _todo_404(TodoNotFoundException e) {
        return new ErrorResponse(e.getId(), e.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse _user_404(AccountNotFoundException e) {
        return new ErrorResponse(e.getUsername(), e.getMessage());
    }

    @ExceptionHandler(AccountDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse _user_400(AccountDuplicateException e) {
        return new ErrorResponse(e.getUsername(), e.getMessage());
    }
}
