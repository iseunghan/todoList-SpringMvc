package me.iseunghan.todolist.controller.user;

import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.common.AuthUtils;
import me.iseunghan.todolist.common.LoginUser;
import me.iseunghan.todolist.model.dto.CreateTodoItemRequest;
import me.iseunghan.todolist.model.dto.RetrieveTodoItemResponse;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserTodoListApiController {

    @Autowired
    private TodoService todoService;

    @GetMapping("/accounts/{username}/todolist")
    public ApiResponse<RetrieveTodoItemResponse> findUserTodoList(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String username, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        RetrieveTodoItemResponse response = todoService.findUserTodoList(pageable, username);

        return ApiResponse.<RetrieveTodoItemResponse>of()
                .success(true)
                .error(null)
                .content(response)
                .build();
    }

    @GetMapping("/accounts/{username}/todolist/{id}")
    public ApiResponse<TodoItemDto> findUserTodo(@PathVariable String username, @PathVariable Long id, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);

        TodoItemDto todoItemDto = todoService.findById(id);

        return ApiResponse.<TodoItemDto>of()
                .success(true)
                .error(null)
                .content(todoItemDto)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/accounts/{username}/todolist")
    public ApiResponse<TodoItemDto> addTodo(@PathVariable String username, @Valid @RequestBody CreateTodoItemRequest request, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);

        TodoItemDto result = todoService.addTodo(username, request);

        return ApiResponse.<TodoItemDto>of()
                .success(true)
                .error(null)
                .content(result)
                .build();
    }

    @PatchMapping("/accounts/{username}/todolist/{id}")
    public ApiResponse<TodoItemDto> updateTodo(@PathVariable String username, @PathVariable Long id, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        TodoItemDto todoItem = todoService.updateStatus(id);

        return ApiResponse.<TodoItemDto>of()
                .success(true)
                .error(null)
                .content(todoItem)
                .build();
    }

    @DeleteMapping("/accounts/{username}/todolist/{id}")
    public ApiResponse<Void> deleteTodo(@PathVariable String username, @PathVariable Long id, @LoginUser String loginUsername) {
        AuthUtils.validationUsername(username, loginUsername);
        todoService.deleteTodoItem(id);

        return ApiResponse.<Void>of()
                .success(true)
                .error(null)
                .content(null)
                .build();
    }
}
