package me.iseunghan.todolist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTodoItemRequest {

    @NotEmpty(message = "title이 비어있습니다.")
    private String title;

}
