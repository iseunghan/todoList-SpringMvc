package me.iseunghan.todolist.controller.advice;

import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.exception.AccessDeniedException;
import me.iseunghan.todolist.exception.BadRequestException;
import me.iseunghan.todolist.exception.GeneralException;
import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.exception.model.ErrorCode;
import me.iseunghan.todolist.exception.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"me.iseunghan.todolist"})
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ApiResponse<Void> allException(GeneralException e) {
        return ApiResponse.<Void>of()
                .success(false)
                .error(ErrorResponse.of(
                        e.getCode(),
                        e.getMessage())
                )
                .content(null)
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> exception_400(BadRequestException e) {
        return ApiResponse.<Void>of()
                .success(false)
                .error(ErrorResponse.of(
                        e.getCode(),
                        e.getMessage())
                )
                .content(null)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> exception_404(NotFoundException e) {
        return ApiResponse.<Void>of()
                .success(false)
                .error(ErrorResponse.of(
                        e.getCode(),
                        e.getMessage())
                )
                .content(null)
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> exception_403(AccessDeniedException e) {
        return ApiResponse.<Void>of()
                .success(false)
                .error(ErrorResponse.of(
                        e.getCode(),
                        e.getMessage())
                )
                .content(null)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> methodArgumentNotValid(MethodArgumentNotValidException e) {

        return ApiResponse.<Void>of()
                .success(false)
                .error(ErrorResponse.of(
                        ErrorCode.FIELD_ERROR.getCode(),
                        ErrorCode.FIELD_ERROR.getMessage(),
                        e.getBindingResult())
                )
                .content(null)
                .build();
    }
}
