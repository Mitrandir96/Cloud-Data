package ru.netology.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.dto.PostLoginRequest;
import ru.netology.service.UserService;

import javax.security.auth.login.LoginException;

@RestController
public class Controller {

    private UserService userService;

    public Controller(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestBody PostLoginRequest request) throws LoginException {
        return userService.login(request.getLogin(), request.getPassword());
    }

}
