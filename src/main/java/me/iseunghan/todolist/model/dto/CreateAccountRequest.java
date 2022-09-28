package me.iseunghan.todolist.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {
    @NotBlank(message = "아이디가 비어있습니다.")
    private String username;

    @NotEmpty(message = "패스워드가 비어있습니다.")
    private String password;

    @NotEmpty(message = "닉네임이 비어있습니다.")
    private String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotEmpty(message = "이메일이 비어있습니다.")
    private String email;
}
