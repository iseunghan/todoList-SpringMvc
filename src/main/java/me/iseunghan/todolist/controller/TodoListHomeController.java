package me.iseunghan.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodoListHomeController {

    @GetMapping(value = "/")
    public String home() {

        return "/todoList/index";
    }
}
