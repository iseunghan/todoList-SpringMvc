package me.iseunghan.todolist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jdk.nashorn.internal.ir.annotations.Ignore;
import me.iseunghan.todolist.controller.TodoListController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class TodoResource extends RepresentationModel {

    @JsonUnwrapped
    private TodoItem todoItem;


    public TodoResource() {
    }

    public TodoResource(TodoItem todoItem, Long userId) {
        this.todoItem = todoItem;
        add(linkTo(TodoListController.class, userId).slash(todoItem.getId()).withSelfRel());    // 컨트롤러에서 하나하나 생성할 수 없으니까 생성자에서 추가!
    }

    public TodoItem getTodoItem() {
        return todoItem;
    }
}
