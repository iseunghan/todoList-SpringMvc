package me.iseunghan.todolist.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountResponse {

    private String username;

    private String email;

    private String nickname;
}
