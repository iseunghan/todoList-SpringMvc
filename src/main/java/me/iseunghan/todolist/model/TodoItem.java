package me.iseunghan.todolist.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

import static lombok.Builder.Default;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TODO_ITEM")
@ToString(exclude = "account")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    @Default
    private TodoStatus status = TodoStatus.NEVER;

    @Column(name = "created_at")
    @Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    public void addAccount(Account account) {
        account.addTodo(this);
        this.account = account;
    }
}
