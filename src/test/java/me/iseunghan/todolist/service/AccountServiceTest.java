package me.iseunghan.todolist.service;

import me.iseunghan.todolist.exception.AccountNotFoundException;
import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.dto.PublicAccountDto;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.dto.AccountDto;
import me.iseunghan.todolist.repository.TodoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TodoService todoService;

    @BeforeAll
    void setup() {
        System.out.println("================= START ===================");

        AccountDto accountDto = AccountDto.builder()
                .id(1L)
                .username("test")
                .email("test@email.com")
                .password("pass")
                .nickname("test-nick")
                .build();

        AccountDto accountDto2 = AccountDto.builder()
                .id(2L)
                .username("test2")
                .email("test2@email.com")
                .password("pass")
                .nickname("test2-nick")
                .build();

        accountService.addAccount(accountDto);
        accountService.addAccount(accountDto2);

        for (int i = 0; i < 10; i++) {
            TodoItemDto todo = TodoItemDto.builder()
                    .title("title" + i)
                    .status(TodoStatus.NEVER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .username(accountDto.getUsername())
                    .build();

            todoService.addTodo(accountDto.getUsername(), todo);
        }

        System.out.println("================= END ===================");
    }

    @Test
    void 하나의_account_조회할_수있다_ADMIN() {
        // when
        Account account = accountService.findAccount_ADMIN("test");

        // then
        assertEquals(account.getUsername(), "test");
        assertNotNull(account.getPassword());
        assertEquals(account.getEmail(), "test@email.com");
        assertEquals(account.getNickname(), "test-nick");
    }

    @Test
    void 하나의_public_account_조회할_수있다_USER() {
        // when
        PublicAccountDto account = accountService.findAccount_USER("test");

        // then
        assertEquals(account.getUsername(), "test");
        assertEquals(account.getEmail(), "test@email.com");
        assertEquals(account.getNickname(), "test-nick");
    }

    @Test
    void 없는_account_조회_404() {
        // given
        String username = "ghost";

        // when & then
        AccountNotFoundException nfe = assertThrows(AccountNotFoundException.class, () -> accountService.findAccount_ADMIN(username));
        assertEquals(nfe.getUsername(), username);
    }

    @Test
    void account_수정할_수있다() {
        // given
        AccountDto accountDto = AccountDto.builder()
                .username("test3")
                .password("pass3")
                .email("test3@email.com")
                .nickname("test3-nick")
                .build();

        // when
        Account account = accountService.updateAccount("test2", accountDto);

        // then
        assertEquals(account.getUsername(), accountDto.getUsername());
        assertEquals(account.getEmail(), accountDto.getEmail());
        assertEquals(account.getNickname(), accountDto.getNickname());
    }

    @Test
    void 없는_account_수정시_404() {
        // given
        String username = "ghost";

        // when & then
        assertThrows(AccountNotFoundException.class, () -> accountService.updateAccount(username, null));
    }

    @Test
    @Order(Integer.MAX_VALUE - 1)
    void account_삭제할_수있다() {
        // when & then
        assertDoesNotThrow(() -> accountService.deleteAccount("test"));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void account_삭제후_할일도_함께_삭제됐는지() {
        // given

        // when
        List<TodoItem> test_todoList = todoService.findAllByUsername("test");
        // then
        assertEquals(test_todoList.size(), 0);
    }

    @Test
    void 없는_account_삭제_404() {
        // given
        String username = "ghost";

        // when & then
        AccountNotFoundException nfe = assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(username));
        assertEquals(nfe.getUsername(), username);
    }


}