package me.iseunghan.todolist.model.dto;

import lombok.*;
import me.iseunghan.todolist.model.TodoItem;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private List<TodoItem> todoList;

}
