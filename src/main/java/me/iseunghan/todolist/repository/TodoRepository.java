package me.iseunghan.todolist.repository;

import me.iseunghan.todolist.model.TodoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<TodoItem, Long> {

    @Query(value = "select t from TodoItem t join fetch t.account where t.account.username = :username",
            countQuery = "select count(t) from TodoItem t join t.account where t.account.username = :username")
    Page<TodoItem> findAllByUsername(String username, Pageable pageable);

    @Query(value = "select t from TodoItem t join fetch t.account",
    countQuery = "select count(t) from TodoItem t join t.account")
    Page<TodoItem> findAllByFetchJoin(Pageable pageable);
}
