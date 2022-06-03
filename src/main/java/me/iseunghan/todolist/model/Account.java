package me.iseunghan.todolist.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<AccountRole> roles;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoItem> todoList;

    public void addTodo(TodoItem todoItem) {
        todoItem.setAccount(this);

        if (todoList == null) {
            todoList = new ArrayList<>();
        }
        todoList.add(todoItem);
    }

    public String getRolesToString() {
        return this.roles.stream()
                .map(AccountRole::toString)
                .collect(Collectors.joining(","));
    }
}
