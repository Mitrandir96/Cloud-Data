package ru.netology.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.dto.PostLoginRequest;
import ru.netology.dto.PostLoginResponse;
import ru.netology.service.UserService;

import javax.security.auth.login.LoginException;

@RestController
public class Controller {

    private UserService userService;

    public Controller(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public PostLoginResponse login(@RequestBody @Validated PostLoginRequest request) throws LoginException {
        return userService.login(request.getLogin(), request.getPassword());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("auth-token") String authToken) throws LoginException {
        userService.logout(authToken);
    }

}
