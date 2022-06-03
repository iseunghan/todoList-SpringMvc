//package me.iseunghan.todolist.model;
//
//import com.fasterxml.jackson.annotation.JsonUnwrapped;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import me.iseunghan.todolist.controller.TodoListController;
//import org.springframework.hateoas.CollectionModel;
//import org.springframework.hateoas.RepresentationModel;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
//
//@NoArgsConstructor
//public class TodoResource extends RepresentationModel<TodoResource> {
//
//    @Getter
//    @JsonUnwrapped
//    private TodoItem todoItem;
//
//    @Builder
//    public TodoResource(TodoItem todoItem) {
//        this.todoItem = todoItem;
//        add(linkTo(TodoListController.class).slash(todoItem.getId()).withSelfRel());
//        add(linkTo(TodoListController.class).slash(todoItem.getId()).withRel("put"));
//        add(linkTo(TodoListController.class).slash(todoItem.getId()).withRel("delete"));
//    }
//
//    public static TodoResource toModel(TodoItem todo) {
//        return new TodoResource(todo);
//    }
//
//    public static CollectionModel<TodoResource> toCollectionModel(List<? extends TodoItem> todos) {
//        return CollectionModel.of(
//                todos.stream()
//                        .map(t -> TodoResource.builder().todoItem(t).build())
//                        .collect(Collectors.toList()));
//    }
//}
