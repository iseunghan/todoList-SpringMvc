package me.iseunghan.todolist.service;

import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.TodoitemDto;
import me.iseunghan.todolist.repository.TodoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * by.승한 - 할일을 추가해주는 메소드
     */
    public TodoItem addTodo(TodoitemDto todoitemDto) {
        if (!StringUtils.hasText(todoitemDto.getTitle())) {
            throw new IllegalStateException("공백을 입력할 수 없습니다.");
        }
        TodoItem todo = modelMapper.map(todoitemDto, TodoItem.class);

        return todoRepository.save(todo);
    }

    /**
     * by.승한 - 전체 할일 목록 조회 (ex. page=0&size=5)
     */
    public Page<TodoItem> findAllPageable(Pageable pageable) {
        return todoRepository.findAll(pageable);
    }

    /**
     * by.승한 - id로 하나의 할일을 조회하는 메소드
     */
    public TodoItem findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NoSuchElementException("존재하지 않는 할일입니다.");
                });
    }

    /**
     * by.승한 - 할일의 상태를 수정(변경)해주는 메소드
     * DONE 일 경우 -> NEVER, NEVER 일 경우 그 반대.
     */
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
            return todoRepository.save(todoItem);
        } else {
            throw new NoSuchElementException("존재하지 않는 할일입니다.");
        }
    }

    /**
     * by.승한 - id로 조회해서 존재할 경우 삭제해주는 메소드
     */
    public void deleteTodoItem(Long id) {
        todoRepository.deleteById(id);
    }
}