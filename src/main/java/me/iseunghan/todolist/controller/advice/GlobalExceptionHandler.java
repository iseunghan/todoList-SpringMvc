package me.iseunghan.todolist.controller.advice;

import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.exception.GeneralException;
import me.iseunghan.todolist.exception.model.ErrorCode;
import me.iseunghan.todolist.exception.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"me.iseunghan.todolist"})
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(GeneralException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(
                        ApiResponse.<Void>of()
                                .success(false)
                                .error(ErrorResponse.of(
                                        e.getCode(),
                                        e.getMessage())
                                )
                                .content(null)
                                .build()
                )
                ;
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
