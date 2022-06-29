package me.iseunghan.todolist.controller.user;

import me.iseunghan.todolist.exception.AccessDeniedException;
import me.iseunghan.todolist.model.dto.RetrieveTodoItemResponse;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/user")
public class UserTodoListApiController {

    @Autowired
    private TodoService todoService;

    @GetMapping("/accounts/{username}/todolist")
    public ResponseEntity findUserTodoList(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String username) {
        RetrieveTodoItemResponse response = todoService.findUserTodoList(pageable, username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts/{username}/todolist/{id}")
    public ResponseEntity findUserTodo(@PathVariable String username, @PathVariable Long id, Authentication authentication) {
        isAuthenticate(authentication, username);

        TodoItemDto todoItemDto = todoService.findById(id);

        return ResponseEntity.ok(todoItemDto);
    }

    @PostMapping("/accounts/{username}/todolist")
    public ResponseEntity addTodo(@PathVariable String username, @RequestBody TodoItemDto todoItemDto, Authentication authentication) {
        isAuthenticate(authentication, username);

        TodoItemDto result = todoService.addTodo(username, todoItemDto);
        URI uri = linkTo(methodOn(UserTodoListApiController.class).addTodo(username, todoItemDto, authentication)).withSelfRel().toUri();

        return ResponseEntity.created(uri).body(result);
    }

    @PatchMapping("/accounts/{username}/todolist/{id}")
    public ResponseEntity updateTodo(@PathVariable String username, @PathVariable Long id, Authentication authentication) {
        isAuthenticate(authentication, username);

        TodoItemDto todoItem = todoService.updateStatus(id);

        return ResponseEntity.ok(todoItem);
    }

    @DeleteMapping("/accounts/{username}/todolist/{id}")
    public ResponseEntity deleteTodo(@PathVariable String username, @PathVariable Long id, Authentication authentication) {
        isAuthenticate(authentication, username);

        Long deleteId = todoService.deleteTodoItem(id);

        return ResponseEntity.ok(deleteId);
    }

    private void isAuthenticate(Authentication authentication, String username) {
        if(!authentication.getName().equals(username))
            throw new AccessDeniedException(username);
    }
}
