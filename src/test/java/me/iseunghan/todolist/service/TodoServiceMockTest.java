package me.iseunghan.todolist.service;

import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoItemDto;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceMockTest {

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    TodoItem getMockTodo(Long id) {
        // given
        return TodoItem.builder()
                .id(id)
                .title("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(TodoStatus.NEVER)
                .build();
    }

    @Test
    void 할일을_저장할수있다() {
        // given
        TodoItem mockTodo = getMockTodo(123L);

        when(todoRepository.save(any(TodoItem.class))).thenReturn(mockTodo);

        // when
        TodoItem result = todoService.addTodo(TodoItemDto.builder().title("test").build());

        // then
        verify(todoRepository, times(1)).save(any(TodoItem.class));
        assertEquals(result, mockTodo);
    }

    @Test
    void 할일을_조회할수있다() {
        // given
        TodoItem mockTodo = getMockTodo(123L);

        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(mockTodo));

        // when
        TodoItem result = todoService.findById(123L);

        // then
        verify(todoRepository, times(1)).findById(anyLong());
        assertEquals(result.getTitle(), "test");
        assertEquals(result, mockTodo);
    }

    @Test
    void 할일이_존재하지않으면_조회할수없다() {
        // given
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> {
            todoService.findById(123L);
        });
        verify(todoRepository, times(1)).findById(anyLong());
        assertEquals(nfe.getId(), 123L);
    }

    @Test
    void 할일_전체를_조회할수있다() {
        // given
        List<TodoItem> mockTodos = new ArrayList<>();
        for (long i = 0; i < 5; i++) mockTodos.add(getMockTodo(i));

        Page<TodoItem> itemPage = new PageImpl<TodoItem>(mockTodos);
        when(todoRepository.findAll((Pageable) any())).thenReturn(itemPage);

        // when
        List<TodoItem> todos = todoService.findAllPageable(any()).getContent();

        // then
        verify(todoRepository, times(1)).findAll((Pageable) any());
        assertNotEquals(todos.size(), 0);
    }

    @Test
    void 할일이_없으면_빈_List를_리턴한다() {
        // given
        Page<TodoItem> itemPage = new PageImpl<>(new ArrayList<>());

        when(todoRepository.findAll((Pageable) any())).thenReturn(itemPage);

        // when
        List<TodoItem> result = todoService.findAllPageable(any()).getContent();

        // then
        verify(todoRepository, times(1)).findAll((Pageable) any());
        assertEquals(result.size(), 0);
    }

    @Test
    void 할일의_상태를_변경할수있다() {
        // given
        TodoItem mockTodo = getMockTodo(123L);
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(mockTodo));

        // when
        TodoItem result = todoService.updateStatus(123L);

        // then
        assertEquals(result.getStatus(), TodoStatus.DONE);
    }

    @Test
    void 할일이_없는경우에는_변경할수없다() {
        // given
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            todoService.updateStatus(123L);
        });
    }

    @Test
    void 할일을_삭제할수있다() {
        // given
        TodoItem mockTodo = getMockTodo(123L);
        when(todoRepository.findById(123L)).thenReturn(Optional.of(mockTodo));

        // when
        Long id = todoService.deleteTodoItem(123L);

        // then
        verify(todoRepository, times(1)).deleteById(123L);
        assertEquals(id, 123L);
    }

    @Test
    void 할일이_없는경우에는_삭제할수없다() {
        // given
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            todoService.deleteTodoItem(123L);
        });
    }
}