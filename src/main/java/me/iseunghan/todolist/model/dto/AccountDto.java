package me.iseunghan.todolist.model.dto;

import lombok.*;

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

    private String roles;

    private List<TodoItemDto> todoList;

}
