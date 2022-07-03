package me.iseunghan.todolist.model.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
public class CreateAccountRequest {
    @NotEmpty(message = "필수값을 입력해주세요.")
    private String username;

    @NotEmpty(message = "필수값을 입력해주세요.")
    private String password;

    @NotEmpty(message = "필수값을 입력해주세요.")
    private String nickname;

    @NotEmpty(message = "필수값을 입력해주세요.")
    private String email;
}
