package me.iseunghan.todolist.service;

import me.iseunghan.todolist.exception.TodoNotFoundException;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.dto.AccountDto;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private AccountService accountService;

    List<TodoItemDto> todoItemDtos = new ArrayList<>();
    List<TodoItem> todoItems = new ArrayList<>();

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
                    .build();

            this.todoItemDtos.add(todo);

            TodoItem todoItem = todoService.addTodo(accountDto.getUsername(), todo);
            todoItems.add(todoItem);
        }

        System.out.println("================= END ===================");
    }

    @Test
    void 할일을_추가할_수_있다() {
        // given
        TodoItemDto todoItemDto = TodoItemDto.builder()
                .title("test title")
                .username("test2")
                .build();

        // when
        TodoItem todoItem = todoService.addTodo(todoItemDto.getUsername(), todoItemDto);

        // then
        assertEquals(todoItem.getTitle(), todoItemDto.getTitle());
    }

    @Test
    void 해당_account_모든_todo_조회() {
        // when
        List<TodoItem> todoList = todoService.findAllByUsername("test");

        // then
        assertNotEquals(todoList.size(), 0);
    }

    @Test
    void 해당_유저의_모든_할일을_조회할_수있다_with_pageable() {
        // given
        int size = 5;
        int page_size = this.todoItemDtos.size() / size;

        // when
        Page<TodoItem> pageResult = todoService.findAllByUsername(PageRequest.of(0, 5), "test");

        // then
        List<TodoItem> content = pageResult.getContent();

        assertEquals(pageResult.getTotalPages(), page_size);
        assertEquals(pageResult.getContent().size(), size);
    }

    @Test
    void 해당_유저의_모든_할일을_조회할_수있다_without_pageable() {
        // when
        List<TodoItem> todoList = todoService.findAllByUsername("test");

        // then
        assertEquals(todoList.size(), 10);
    }

    @Test
    void 할일을_상태를_변경할_수있다() {
        // given
        Long id = this.todoItems.get(0).getId();

        // when
        TodoItem todoItem = todoService.updateStatus(id);

        // then
        assertEquals(todoItem.getStatus(), TodoStatus.DONE);
    }

    @Test
    void 없는_할일을_수정하면_404() {
        // given
        Long id = 90L;

        // when & then
        TodoNotFoundException nfe = assertThrows(TodoNotFoundException.class, () -> todoService.updateStatus(id));
        assertEquals(nfe.getId(), id);
    }

    @Test
    void 할일을_삭제할_수있다() {
        // given
        Long id = this.todoItems.get(0).getId();

        // when & then
        Long delete_id = assertDoesNotThrow(() -> todoService.deleteTodoItem(id));
        assertEquals(delete_id, id);
    }

    @Test
    void 없는_할일을_삭제하면_404() {
        // given
        Long id = 90L;

        // when & then
        TodoNotFoundException nfe = assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodoItem(id));
        assertEquals(nfe.getId(), id);
    }
}
