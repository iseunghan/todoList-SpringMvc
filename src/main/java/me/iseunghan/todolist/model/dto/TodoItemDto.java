package me.iseunghan.todolist.model.dto;

import lombok.*;
import lombok.Builder.Default;
import me.iseunghan.todolist.model.TodoStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoItemDto {

    private Long id;

    private String title;

    @Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Default
    private TodoStatus status = TodoStatus.NEVER;

    private String username;

}
