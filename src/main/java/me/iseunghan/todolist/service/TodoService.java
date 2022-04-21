package me.iseunghan.todolist.service;

import me.iseunghan.todolist.exception.NotEmptyException;
import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoItemDto;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.repository.TodoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ModelMapper modelMapper;

    public TodoItem addTodo(TodoItemDto todoitemDto) {
        if (!StringUtils.hasText(todoitemDto.getTitle())) {
            throw new NotEmptyException(todoitemDto.getId());
        }
        TodoItem todo = modelMapper.map(todoitemDto, TodoItem.class);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        return todoRepository.save(todo);
    }

    public Page<TodoItem> findAllPageable(Pageable pageable) {
        return todoRepository.findAll(pageable);
    }

    public TodoItem findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException(id);
                });
    }

    @Transactional
    public TodoItem updateStatus(Long id) {
        Optional<TodoItem> todoItemOptional = todoRepository.findById(id);

        if (todoItemOptional.isPresent()) {
            TodoItem todoItem = todoItemOptional.get();

            if (todoItem.getStatus().equals(TodoStatus.NEVER)) {
                todoItem.setStatus(TodoStatus.DONE);
            } else {
                todoItem.setStatus(TodoStatus.NEVER);
            }
            todoItem.setUpdatedAt(LocalDateTime.now());
            return todoItem;
        } else {
            throw new NotFoundException(id);
        }
    }

    public Long deleteTodoItem(Long id) {
        todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        todoRepository.deleteById(id);

        return id;
    }
}