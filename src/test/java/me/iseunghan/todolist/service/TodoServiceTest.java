package me.iseunghan.todolist.service;

import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.User;
import me.iseunghan.todolist.repository.TodoRepository;
import me.iseunghan.todolist.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TodoServiceTest {

    @Autowired

    TodoService todoService;
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("할일 추가 기능 테스트")
    void addTodoTest() {
        // given
        User user = new User();
        user.setName("name");
        user.setEmail("email@naver.com");
        user.setRole("ROLE_USER");
        userRepository.save(user);
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle("스프링 공부하기");
        todoItem.setDate(LocalDate.parse("2020-01-25"));
        todoItem.setStatus(TodoStatus.NEVER);

        // when
        TodoItem todoItem2 = todoService.addTodo(todoItem, user.getId());

        // then
        assertEquals(todoItem2.getTitle(), todoItem.getTitle());
        // assertTrue(todoItem==todoItem1);
        // 객체 비교하면 안되나? -> jpa는 동일 보장을 한다고 들얼ㅆ는
    }

    @Test
    @DisplayName("할일 목록 조회 테스트")
    void getTodoItemListTest() {
        // given
        User user = new User();
        user.setName("name");
        user.setEmail("email@naver.com");
        user.setRole("ROLE_USER");
        userRepository.save(user);
        TodoItem todoItem1 = new TodoItem();
        todoItem1.setTitle("자바 공부하기");
        todoItem1.setDate(LocalDate.parse("2020-01-25"));
        todoItem1.setStatus(TodoStatus.NEVER);

        TodoItem todoItem2 = new TodoItem();
        todoItem2.setTitle("C++ 공부하기");
        todoItem2.setDate(LocalDate.parse("1990-12-13"));
        todoItem2.setStatus(TodoStatus.NEVER);

        todoService.addTodo(todoItem1, user.getId());
        todoService.addTodo(todoItem2, user.getId());

        // when
        List<TodoItem> list = todoService.getTodoItemList();

        // then
        TodoItem listItem1 = list.get(0);
        TodoItem listItem2 = list.get(1);

        assertEquals(listItem1.getTitle(), todoItem1.getTitle());
        assertEquals(listItem1.getDate(), todoItem1.getDate());
        assertEquals(listItem2.getTitle(), todoItem2.getTitle());
        assertEquals(listItem2.getDate(), todoItem2.getDate());
    }

    @Test
    @DisplayName("날짜로 조회 테스트")
    void findTodoListByDateTest() {
        // given
        User user = new User();
        user.setName("name");
        user.setEmail("email@naver.com");
        user.setRole("ROLE_USER");
        userRepository.save(user);

        TodoItem todoItem1 = new TodoItem();
        todoItem1.setTitle("자바 공부하기");
        LocalDate date = LocalDate.of(2020, 01, 25);
        todoItem1.setDate(date);
        todoItem1.setStatus(TodoStatus.NEVER);

        TodoItem todoItem2 = new TodoItem();
        todoItem2.setTitle("C++ 공부하기");
        todoItem2.setDate(date);
        todoItem2.setStatus(TodoStatus.NEVER);

        TodoItem todoItem3 = new TodoItem();
        todoItem3.setTitle("JPA 공부하기");
        LocalDate date1 = LocalDate.of(2019, 11, 30);
        todoItem3.setDate(date1);
        todoItem3.setStatus(TodoStatus.NEVER);

        todoService.addTodo(todoItem1, user.getId());
        todoService.addTodo(todoItem2, user.getId());
        todoService.addTodo(todoItem3, user.getId());

        // when
        List<TodoItem> list = todoService.findTodoListByDate(date);

        // then
        System.out.println(list.get(0).getDate());
        System.out.println(list.get(1).getDate());
        assertThat(list.get(0).getDate()).isEqualTo(todoItem1.getDate());
        assertThat(list.get(1).getDate()).isEqualTo(todoItem2.getDate());
        assertThat(list.get(0).getDate()).isNotEqualTo(date1);
    }

    @Test
    void updateStatusTest() {
        // Given
        User user = new User();
        user.setName("name");
        user.setEmail("email@naver.com");
        user.setRole("ROLE_USER");
        userRepository.save(user);

        TodoItem todoItem = new TodoItem();
        todoItem.setTitle("책 읽기");
        todoItem.setStatus(TodoStatus.NEVER);
        todoItem.setDate(LocalDate.now());
        todoService.addTodo(todoItem, user.getId());

        // When
        todoService.updateStatus(todoItem.getId());

        // Then
        TodoItem todoItem1 = todoRepository.findById(todoItem.getId()).get();
        assertEquals(TodoStatus.DONE, todoItem1.getStatus());
    }
}
