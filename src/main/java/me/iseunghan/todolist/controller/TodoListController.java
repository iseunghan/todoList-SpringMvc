package me.iseunghan.todolist.controller;

import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoListForm;
import me.iseunghan.todolist.service.TodoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/todoList")
public class TodoListController {

    // 필드로 DI(의존성 주입)를 하게 되면 final로 선언이 안된다. (final로 하고 싶다면, 생성자 DI 사용)
    private final TodoService todoService;
    private final ModelMapper modelMapper;

    public TodoListController(TodoService todoService, ModelMapper modelMapper) {
        this.todoService = todoService;
        this.modelMapper = modelMapper;
    }

    /**
     * by.승한 - "/todoList/new"에 대한 GET방식 메소드입니다.
     * @return 할일 생성 폼으로 이동합니다.
     */
    @GetMapping("/new")
    public String createForm() {

        return "todoList/createTodoListForm";
    }

    /**
     * by.승한 - 폼에서 받은 todoListForm 객체를 받아서
     * @param form 을 todoItem으로 mapping시켜서 service로 넘겨서 추가합니다.
     * @return 다시 홈화면으로 이동합니다.
     */
    @PostMapping(value = "/new")
    public String create(TodoListForm form) {
        if (form != null) {
            TodoItem map = modelMapper.map(form, TodoItem.class); // modelMapper를 사용하면 코드를 간결하게 줄일 수 있음!
            todoService.addTodo(map);
        }
        return "redirect:/";
    }

    /**
     * by.승한 - id에 해당하는 status를 DONE으로 변경합니다.
     * @param id
     * @return 홈화면으로 이동합니다.
     */
    @GetMapping(value = "/done/{id}")
    public String updateDoneStatus(@PathVariable("id") Long id) {
        todoService.updateStatus(id);

        return "redirect:/";
    }

    /**
     * by.승한 - id에 해당하는 status를 NEVER로 변경합니다.
     * @param id
     * @return 홈화면으로 이동합니다.
     */
    @GetMapping(value = "/never/{id}")
    public String updateNeverStatus(@PathVariable("id") Long id) {
        todoService.updateStatus(id);

        return "redirect:/";
    }

    /**
     * by.승한 - 해당하는 id를 삭제합니다.
     * @param id
     * @return 홈화면으로 이동합니다.
     */
    @GetMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        todoService.deleteTodoItem(id);

        return "redirect:/";
    }
}