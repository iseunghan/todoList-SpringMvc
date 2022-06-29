package me.iseunghan.todolist.service;

import me.iseunghan.todolist.exception.TodoNotFoundException;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.dto.CreateAccountRequest;
import me.iseunghan.todolist.model.dto.RetrieveTodoItemResponse;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    List<TodoItemDto> savedTodoDtos = new ArrayList<>();

    @BeforeAll
    void setup() {
        System.out.println("================= START ===================");

        CreateAccountRequest request = CreateAccountRequest.builder()
                .username("test")
                .email("test@email.com")
                .password("pass")
                .nickname("test-nick")
                .build();

        CreateAccountRequest request2 = CreateAccountRequest.builder()
                .username("test2")
                .email("test2@email.com")
                .password("pass")
                .nickname("test2-nick")
                .build();

        accountService.addAccount(request);
        accountService.addAccount(request2);

        for (int i = 0; i < 10; i++) {
            TodoItemDto todo = TodoItemDto.builder()
                    .title("title" + i)
                    .status(TodoStatus.NEVER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            this.todoItemDtos.add(todo);

            TodoItemDto save = todoService.addTodo(request.getUsername(), todo);
            savedTodoDtos.add(save);
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
        TodoItemDto result = todoService.addTodo(todoItemDto.getUsername(), todoItemDto);

        // then
        assertEquals(result.getTitle(), todoItemDto.getTitle());
    }

    @Test
    void 해당_유저의_모든_할일을_조회할_수있다_with_pageable() {
        // given
        int size = 5;
        int page_size = this.todoItemDtos.size() / size;

        // when
        RetrieveTodoItemResponse result = todoService.findUserTodoList(PageRequest.of(0, 5), "test");

        // then
        assertEquals(result.getPageable().getTotalPages(), page_size);
        assertEquals(result.getTodoList(), size);
    }

    @Test
    void 하나의_할일을_조회할_수있다() {
        // when
        TodoItemDto todo = todoService.findById(1L);

        // then
        assertNotNull(todo.getTitle());
        assertNotNull(todo.getStatus());
        assertNotNull(todo.getUpdatedAt());
        assertNotNull(todo.getCreatedAt());
    }

    @Test
    void 없는_할일을_조회하면_404() {
        // when
        TodoNotFoundException nfe = assertThrows(TodoNotFoundException.class, () -> todoService.findById(999L));

        // then
        assertEquals(nfe.getId(), 999);
    }

    @Test
    void 할일을_상태를_변경할_수있다() {
        // given
        Long id = this.savedTodoDtos.get(0).getId();

        // when
        TodoItemDto todoItem = todoService.updateStatus(id);

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
        Long id = this.savedTodoDtos.get(0).getId();

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
