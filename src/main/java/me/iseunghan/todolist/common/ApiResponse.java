package me.iseunghan.todolist.common;

import lombok.Builder;
import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorResponse;

/**
 * 공통 응답 객체
 */
@Builder(builderMethodName = "of", builderClassName = "of")
public class APIResponse<T> {

    private int status;
    private ErrorResponse error;
    private T message;
}
