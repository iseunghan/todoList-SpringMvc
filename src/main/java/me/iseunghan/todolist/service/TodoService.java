package me.iseunghan.todolist.service;

import lombok.RequiredArgsConstructor;
import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.exception.model.ErrorCode;
import me.iseunghan.todolist.model.Account;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.dto.CreateTodoItemRequest;
import me.iseunghan.todolist.model.dto.PageDto;
import me.iseunghan.todolist.model.dto.RetrieveTodoItemResponse;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.repository.TodoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @Transactional
    public TodoItemDto addTodo(String username, CreateTodoItemRequest request) {
        Account account = accountService.findByUsername(username);

        TodoItem todoItem = TodoItem.builder()
                .title(request.getTitle())
                .status(TodoStatus.NEVER)
                .account(account)
                .build();
        todoItem.addAccount(account);

        TodoItem save = todoRepository.save(todoItem);

        return modelMapper.map(save, TodoItemDto.class);
    }

    public RetrieveTodoItemResponse findAll(Pageable pageable) {
        Page<TodoItem> todoPage = todoRepository.findAll(pageable);

        return RetrieveTodoItemResponse.builder()
                .todoList(todoPage.getContent().stream()
                        .map(t -> {
                            TodoItemDto dto = modelMapper.map(t, TodoItemDto.class);
                            return dto;
                        }).collect(Collectors.toList()))
                .pageable(PageDto.builder()
                        .number(todoPage.getNumber())
                        .totalPages(todoPage.getTotalPages())
                        .totalElements(todoPage.getTotalElements())
                        .first(todoPage.isFirst())
                        .last(todoPage.isLast())
                        .build())
                .build();
    }

    public Page<TodoItem> findAllFetchJoin(Pageable pageable) {
        return todoRepository.findAllByFetchJoin(pageable);
    }

    public RetrieveTodoItemResponse findUserTodoList(Pageable pageable, String username) {

        Page<TodoItem> todoPage = todoRepository.findAllByUsername(username, pageable);

        return RetrieveTodoItemResponse.builder()
                .todoList(todoPage.getContent().stream()
                        .map(t -> {
                            TodoItemDto dto = modelMapper.map(t, TodoItemDto.class);
                            return dto;
                        }).collect(Collectors.toList()))
                .pageable(PageDto.builder()
                        .number(todoPage.getNumber())
                        .totalPages(todoPage.getTotalPages())
                        .totalElements(todoPage.getTotalElements())
                        .first(todoPage.isFirst())
                        .last(todoPage.isLast())
                        .build())
                .build();
    }

    public TodoItemDto findById(Long id) {
        TodoItem todo = todoRepository.findByIdWithFetchJoin(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_TODO));

        return modelMapper.map(todo, TodoItemDto.class);
    }

    @Transactional
    public TodoItemDto updateStatus(Long id) {
        TodoItem todoItem = todoRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException(ErrorCode.NOT_FOUND_TODO);
                });

        // TODO todoDto로 전체 변경할 수 있도록!
        if (todoItem.getStatus().equals(TodoStatus.NEVER)) {
            todoItem.setStatus(TodoStatus.DONE);
        } else {
            todoItem.setStatus(TodoStatus.NEVER);
        }

        return modelMapper.map(todoItem, TodoItemDto.class);
    }

    public Long deleteTodoItem(Long id) {
        todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_TODO));
        todoRepository.deleteById(id);

        return id;
    }
}