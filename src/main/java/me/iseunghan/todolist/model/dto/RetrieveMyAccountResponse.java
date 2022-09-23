package me.iseunghan.todolist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RetrieveMyAccountResponse {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String roles;
}
