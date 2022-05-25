package me.iseunghan.todolist.service;

import lombok.RequiredArgsConstructor;
import me.iseunghan.todolist.exception.NotEmptyException;
import me.iseunghan.todolist.exception.TodoNotFoundException;
import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.repository.TodoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @Transactional
    public TodoItem addTodo(String username, TodoItemDto todoitemDto) {
        if (!StringUtils.hasText(todoitemDto.getTitle())) {
            throw new NotEmptyException(todoitemDto.getId());
        }

        Account account = accountService.findMyAccount(username);

        TodoItem todo = modelMapper.map(todoitemDto, TodoItem.class);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        todo.setStatus(TodoStatus.NEVER);
        todo.addAccount(account);

        return todoRepository.save(todo);
    }

    public Page<TodoItem> findAll(Pageable pageable) {
        return todoRepository.findAll(pageable);
    }

    public Page<TodoItem> findAllFetchJoin(Pageable pageable) {
        return todoRepository.findAllByFetchJoin(pageable);
    }

    public Page<TodoItem> findUserTodoList(Pageable pageable, String username) {
        return todoRepository.findAllByUsername(username, pageable);
    }

    public TodoItem findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> {
                    throw new TodoNotFoundException(id);
                });
    }

    @Transactional
    public TodoItem updateStatus(Long id) {
        TodoItem todoItem = todoRepository.findById(id)
                .orElseThrow(() -> {
                    throw new TodoNotFoundException(id);
                });

        // TODO todoDto로 전체 변경할 수 있도록!
        if (todoItem.getStatus().equals(TodoStatus.NEVER)) {
            todoItem.setStatus(TodoStatus.DONE);
        } else {
            todoItem.setStatus(TodoStatus.NEVER);
        }
        todoItem.setUpdatedAt(LocalDateTime.now());
        return todoItem;
    }

    public Long deleteTodoItem(Long id) {
        todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        todoRepository.deleteById(id);

        return id;
    }
}