package me.iseunghan.todolist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicAccountDto {

    private String username;

    private String email;

    private String nickname;

}
