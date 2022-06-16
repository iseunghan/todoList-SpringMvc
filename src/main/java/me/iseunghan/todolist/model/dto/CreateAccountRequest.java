package me.iseunghan.todolist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
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
