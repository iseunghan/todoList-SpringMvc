package me.iseunghan.todolist.controller;

import me.iseunghan.todolist.model.*;
import me.iseunghan.todolist.service.TodoService;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/todoLists", produces = MediaTypes.HAL_JSON_VALUE)   // TODO rest API를 위해 resource를 복수형으로 변경
public class TodoListController {

    // 필드로 DI(의존성 주입)를 하게 되면 final로 선언이 안된다. (final로 하고 싶다면, 생성자 DI 사용)
    private final TodoService todoService;
    private final ModelMapper modelMapper;

    public TodoListController(TodoService todoService, ModelMapper modelMapper) {
        this.todoService = todoService;
        this.modelMapper = modelMapper;
    }

    /**
     * by.승한 - URI에 포함된 id로 하나의 할일을 조회하는 컨트롤러 입니다.
     *
     * @param id
     * @return 200 상태코드와, HTTP body부분에 객체를 json형태로 담아서 응답을 보냅니다. (link정보에는 자신의 링크와, 수정, 삭제로 가는 링크가 담겨있습니다.)
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity selectOne(@PathVariable Long id) {
        TodoItem item = todoService.findOne(id);
        TodoResource todoResource = new TodoResource(item);
        todoResource.add(linkTo(TodoListController.class).slash(item.getId()).withRel("put"));
        todoResource.add(linkTo(TodoListController.class).slash(item.getId()).withRel("delete"));

        return ResponseEntity.ok(todoResource);
    }

    /**
     * by.승한 - 모든 할일을 조회하는 컨트롤러입니다.
     * 모든 할일의 _links에는 self링크와 수정(put), 삭제(delete) 링크를 담아서 응답하고 있습니다.
     *
     * @return 200 응답을 보내고, CollectionModel로 감싼 TodoResourceList를 보냅니다.
     */
    @GetMapping
    public ResponseEntity selectAll() {
        List<TodoItem> todoItemList = todoService.getTodoItemList();
        // TODO 각각의 링크 정보들 넣기. (질문): json 데이터를 보면, todoREsourceList라고 나오는데 괜찮은가.. (TodoItemList가 맞지 않은가?)

        List<TodoResource> resourceList = new ArrayList<>();
        TodoResource todoResource;
        for (TodoItem todoItem : todoItemList) {
            todoResource = new TodoResource(todoItem);
            todoResource.add(linkTo(TodoListController.class).slash(todoItem.getId()).withRel("put"));
            todoResource.add(linkTo(TodoListController.class).slash(todoItem.getId()).withRel("delete"));
            resourceList.add(todoResource);
        }
        CollectionModel<TodoResource> collectionModel = CollectionModel.of(resourceList);
        // list형태로 던지면, 한번 더 감싸지지 않고 나오는 반면, todoresource를 던지면 감싸지게 나온다.
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * by.승한 - 할일을 추가하는 POST 요청이 들어오면 service로 보내서 할일을 추가하도록 합니다.
     *
     * @param todoitemDto json형태로 넘어온 requestbody에 title을 jackson라이브러리가 todoitemDto의
     *                       setTitle을 호출해 매핑을 시켜준다.(그래서 dto는 java bean규약을 준수해야한다.)
     * @return 201 응답을 URI와 보내고, 본문에 저장된 객체를 hal+json으로 보냅니다.
     * @RequestBody를 이용해서, HTTP 요청 body를 자바 객체로 받을 수 있습니다.
     */
    @PostMapping
    public ResponseEntity createTodo(@RequestBody TodoitemDto todoitemDto) {
        // @ModelAttribute(객체일때)와 @RequestParam(String,int등)은 생략이 가능하다.
        // 스프링은 객체이면 @ModelAttribute가 생략됐다고, 단순타입(String,int)면 @RequestParam이 생략됐다고 판단한다.
        System.out.println(todoitemDto.getTitle());
        TodoItem item = modelMapper.map(todoitemDto, TodoItem.class);
        todoService.addTodo(item);

        URI uri = linkTo(TodoListController.class).slash(item.getId()).toUri();
        TodoResource todoResource = new TodoResource(item);

        return ResponseEntity.created(uri).body(todoResource);
    }

    /**
     * by.승한 - 해당하는 id의 할일의 상태(status)를 변경합니다.
     *                          (DONE -> NEVER, NEVER -> DONE)
     * @param id 변경하려는 id를 받습니다.
     * @return 200 응답과 본문에 변경된 내용을 담아 보냅니다.
     */
    @PatchMapping(value = "/{id}")
    public ResponseEntity updateStatus(@PathVariable("id") Long id) {
        todoService.updateStatus(id);
        TodoItem one = todoService.findOne(id);

        TodoResource resource = new TodoResource(one);
        return ResponseEntity.ok(resource);
    }

    /**
     * by.승한 - 해당하는 id의 할일을 삭제합니다.
     *
     * @param id 삭제하려는 id를 받습니다.
     * @return 200 응답을 보냅니다.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteTodo(@PathVariable("id") Long id) {
        todoService.deleteTodoItem(id);

        return ResponseEntity.ok().build();
    }

    /**
     * by.승한 - service에서 할일을 조회할 때, 없는 할일을 조회할 경우 발생하는 예외를 잡는 핸들러입니다.
     *
     * @param e
     * @return 예외가 발생하면, 404 NotFound 응답을 보냅니다.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity exceptionHandler(Exception e) {
        e.printStackTrace();
        return ResponseEntity.notFound().build();
    }
}