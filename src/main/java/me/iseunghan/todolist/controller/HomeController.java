package me.iseunghan.todolist.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController implements ErrorController {

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
    public String adminPage() {
        return "/todoList/admin-page";
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/error")
    public String error(HttpServletRequest request) {
        Integer errorCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        switch (errorCode){
            case 401:
                return "/todoList/unauthorized";
            case 403:
                return "/todoList/access-denied";
            case 404:
                return "/todoList/not-found";
            default:
                return "/todoList/server-error";
        }
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}