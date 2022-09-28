package me.iseunghan.todolist.model;

import lombok.*;

import javax.persistence.*;

import static lombok.Builder.Default;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TODO_ITEM")
@ToString(exclude = "account")
public class TodoItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    @Default
    private TodoStatus status = TodoStatus.NEVER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    public void addAccount(Account account) {
        account.addTodo(this);
        this.account = account;
    }
}
