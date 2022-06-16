package me.iseunghan.todolist.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Authentication authentication) {
        System.out.println("HELLO: " + authentication); // 정상

        return "/todoList/user-page";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/todoList/login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "/todoList/signup";
    }

    @GetMapping("/user")
    public String userPage() {
        return "/todoList/user-page";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String adminPage() {
        return "/todoList/admin-page";
    }
}