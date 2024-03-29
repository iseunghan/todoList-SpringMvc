package me.iseunghan.todolist.repository;

import me.iseunghan.todolist.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "select a from Account a join fetch a.roles join fetch a.todoList",
            countQuery = "select count(a) from Account a join a.roles join a.todoList")
    Page<Account> findAll_ADMIN(Pageable pageable);

    @Query(value = "select a from Account a",
            countQuery = "select count(a) from Account a")
    Page<Account> findAll_USER(Pageable pageable);

    @Query("select a from Account a join fetch a.roles where a.username = :username")
    Optional<Account> findByUsername(String username);

    @Query("select a from Account a join fetch a.roles join fetch a.todoList where a.username = :username")
    Optional<Account> findByUsernameWithTodoList(String username);
}
