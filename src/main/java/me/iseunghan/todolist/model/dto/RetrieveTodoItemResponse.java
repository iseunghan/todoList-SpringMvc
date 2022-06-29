package me.iseunghan.todolist.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RetrieveTodoItemResponse {

    private List<TodoItemDto> todoList;

    private PageDto pageable;
}
