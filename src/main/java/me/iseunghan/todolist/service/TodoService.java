package me.iseunghan.todolist.service;

import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.User;
import me.iseunghan.todolist.repository.TodoRepository;
import me.iseunghan.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * by.승한 - 할일을 추가해주는 메소드
     */
    public TodoItem addTodo(TodoItem todoItem, Long userId) {
        User user = userRepository.findById(userId).get();
        try {
            if (todoItem.getTitle() == "") {
                // 공백일 때
                throw new IllegalStateException("공백을 입력할 수 없습니다.");
            }
            todoItem.setUser(user);
            return todoRepository.save(todoItem);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return todoItem;
    }

    /**
     * by.승한 - 전체 할일 목록 조회
     */
    public List<TodoItem> getTodoItemList() {
        return todoRepository.findAll();
    }

    public List<TodoItem> getTodoItemListByUserId(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    /**
     * by.승한 - id로 하나의 할일을 조회하는 메소드
     */
    public TodoItem findOne(Long id) {
        Optional<TodoItem> todoItemOptional = todoRepository.findById(id);

        if (todoItemOptional.isPresent()) {
            TodoItem todoItem = todoItemOptional.get();

            return todoItem;
        } else {
            throw new NoSuchElementException("존재하지 않는 할일입니다.");
        }
    }

    /**
     * by.승한 - 날짜로 할일을 조회하는 메소드
     */
    public List<TodoItem> findTodoListByDate(LocalDate date) {
        List<TodoItem> list = todoRepository.findByDate(date);
        if (list.isEmpty()) {
            // 없을 때, view 처리를 어떻게 해줄까?
            // -> null을 던지면 타임리프 if문에 걸려서 "할일이 없다고 표시"
        }
        return list;
    }

    /**
     * by.승한 - 할일의 상태를 수정(변경)해주는 메소드
     *          DONE 일 경우 -> NEVER, NEVER 일 경우 그 반대.
     */
    public void updateStatus(Long id) {
        Optional<TodoItem> todoItemOptional = todoRepository.findById(id);

        if (todoItemOptional.isPresent()) {
            TodoItem todoItem = todoItemOptional.get();

            if(todoItem.getStatus().equals(TodoStatus.NEVER)) {
                todoItem.setStatus(TodoStatus.DONE);
            }else{
                todoItem.setStatus(TodoStatus.NEVER);
            }
            todoRepository.save(todoItem);
        } else {
            throw new NoSuchElementException("존재하지 않는 할일입니다.");
        }
    }

    /**
     * by.승한 - id로 조회해서 존재할 경우 삭제해주는 메소드
     */
    public void deleteTodoItem(Long id) {
        Optional<TodoItem> optional = todoRepository.findById(id);

        if (optional.isPresent()) {
            TodoItem todoItem = optional.get();
            todoRepository.delete(todoItem);
        } else {
            throw new NoSuchElementException("존재하지 않는 할일입니다.");
        }
    }
}