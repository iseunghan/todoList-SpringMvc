package me.iseunghan.todolist.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateAccountRequest {

    private String username;

    private String password;

    private String email;

    private String nickname;

}
