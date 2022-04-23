package me.iseunghan.todolist.controller;

import me.iseunghan.todolist.assembler.TodoResourceAssembler;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoItemDto;
import me.iseunghan.todolist.model.TodoResource;
import me.iseunghan.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/todoList")
public class TodoListController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoResourceAssembler todoResourceAssembler;

    @GetMapping(value = "/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        TodoItem todo = todoService.findById(id);

        return ResponseEntity.ok(TodoResource.builder()
                .todoItem(todo)
                .build());
    }

    @GetMapping
    public ResponseEntity<PagedModel<TodoResource>> findAll(@PageableDefault(page = 0, size = 5) Pageable pageable,
                                                            PagedResourcesAssembler<TodoItem> assembler) {

        Page<TodoItem> itemPage = todoService.findAllPageable(pageable);
        PagedModel<TodoResource> todoResources = assembler.toModel(itemPage, todoResourceAssembler);

        return ResponseEntity.ok(todoResources);
    }

    @PostMapping
    public ResponseEntity createTodo(@RequestBody TodoItemDto todoitemDto) {
        TodoItem todo = todoService.addTodo(todoitemDto);

        URI uri = linkTo(TodoListController.class).slash(todo.getId()).toUri();

        return ResponseEntity.created(uri)
                .body(TodoResource.builder()
                        .todoItem(todo)
                        .build());
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity updateStatus(@PathVariable Long id) {
        TodoItem todo = todoService.updateStatus(id);

        return ResponseEntity.ok(TodoResource.builder()
                .todoItem(todo)
                .build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteTodo(@PathVariable Long id) {
        Long deleteId = todoService.deleteTodoItem(id);

        return ResponseEntity.ok(deleteId);
    }

}