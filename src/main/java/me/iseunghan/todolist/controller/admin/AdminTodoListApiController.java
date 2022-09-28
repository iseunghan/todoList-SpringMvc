package me.iseunghan.todolist.controller.admin;

import me.iseunghan.todolist.common.ApiResponse;
import me.iseunghan.todolist.model.dto.RetrieveTodoItemResponse;
import me.iseunghan.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminTodoListApiController {

    @Autowired
    private TodoService todoService;

    @GetMapping("/accounts/todolist")
    public ApiResponse<RetrieveTodoItemResponse> getTodoList(@PageableDefault Pageable pageable) {
        RetrieveTodoItemResponse todoList = todoService.findAll(pageable);

        return ApiResponse.<RetrieveTodoItemResponse>of()
                .success(true)
                .error(null)
                .content(todoList)
                .build();
    }
}
