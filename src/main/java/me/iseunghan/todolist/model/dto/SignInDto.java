package me.iseunghan.todolist.model.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class SignInDto {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
