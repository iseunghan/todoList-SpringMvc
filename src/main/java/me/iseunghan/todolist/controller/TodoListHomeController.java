package me.iseunghan.todolist.controller;

import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.print.Pageable;
import java.util.List;

@Controller
public class TodoListHomeController {

    @Autowired
    private TodoService todoService;

    /**
     * by.승한 - localhost:8080/ 으로 접근 했을 때 출력되는 홈 화면입니다. ("static/index.html" 이 있어도 우선적으로 출력됨)
     */
    @GetMapping(value = "/")
    public String home(Model model) {
        List<TodoItem> list = todoService.getTodoItemList();
        model.addAttribute("todoLists", list);

        return "/todoList/index";
    }
}
