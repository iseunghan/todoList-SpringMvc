package me.iseunghan.todolist.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAccountDto {
    private String username;

    private String email;

    private String nickname;

    private int todoSize;

    private String role;
}