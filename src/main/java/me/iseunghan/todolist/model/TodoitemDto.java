package me.iseunghan.todolist.model;

import lombok.*;
import lombok.Builder.Default;

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
}
