package me.iseunghan.todolist.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicAccountDto {

    private String username;

    private String email;

    private String nickname;

    private int todoSize;
}
