package me.iseunghan.todolist.controller;

import me.iseunghan.todolist.model.User;
import me.iseunghan.todolist.repository.UserRepository;
import me.iseunghan.todolist.security.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class TodoListHomeController {

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private UserRepository userRepository;

    /**
     * by.승한 - localhost:8080 으로 접근 했을 때 출력되는 홈 화면입니다. ("static/index.html" 이 있어도 우선적으로 출력됨)
     */
    @GetMapping(value = "/")
    public String home(SessionUser user, Model model) {
        user = (SessionUser) httpSession.getAttribute("user");

        User user1 = userRepository.findByEmail(user.getEmail()).get();
        model.addAttribute("userid", user1.getId());
        model.addAttribute("name", user.getName());
        model.addAttribute("image", user.getImage());
        return "/todoList/index";
    }

    @GetMapping(value = "/loginForm")
    public String login() {

        return "/todoList/custom-login";
    }
}
