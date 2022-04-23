package me.iseunghan.todolist.assembler;

import me.iseunghan.todolist.controller.TodoListController;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoResource;
import org.modelmapper.internal.util.Lists;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class TodoResourceAssembler extends RepresentationModelAssemblerSupport<TodoItem, TodoResource> {

    public TodoResourceAssembler() {
        super(TodoListController.class, TodoResource.class);
    }

    @Override
    public TodoResource toModel(TodoItem entity) {
        return TodoResource.toModel(entity);
    }

    @Override
    public CollectionModel<TodoResource> toCollectionModel(Iterable<? extends TodoItem> entities) {

        return TodoResource.toCollectionModel(Lists.from(entities.iterator()));
    }
}
