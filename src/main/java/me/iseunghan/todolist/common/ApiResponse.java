package me.iseunghan.todolist.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import me.iseunghan.todolist.exception.model.ErrorResponse;

/**
 * 공통 응답 객체
 */
@Getter
@Builder(builderMethodName = "of", builderClassName = "of")
public class ApiResponse<T> {

    private final Boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final ErrorResponse error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T content;

    ApiResponse(Boolean success, ErrorResponse error, T content) {
        this.success = success;
        this.error = error;
        this.content = content;
    }

}
