package ru.netology.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.PostLoginRequest;
import ru.netology.dto.PostLoginResponse;
import ru.netology.service.FileService;
import ru.netology.service.UserService;

import javax.security.auth.login.LoginException;
import javax.security.auth.message.AuthException;
import java.io.IOException;

@RestController
public class Controller {

    private final UserService userService;
    private final FileService fileService;

    public Controller(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostMapping("/login")
    public PostLoginResponse login(@RequestBody @Validated PostLoginRequest request) throws LoginException {
        return userService.login(request.getLogin(), request.getPassword());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("auth-token") String authToken) {
        userService.logout(authToken);
    }

    @PostMapping("/file")
    public void uploadFile(@RequestHeader("auth-token") String authToken, @RequestPart String hash, @RequestPart MultipartFile file, @RequestParam String filename) throws AuthException, IOException, IllegalArgumentException {
        fileService.uploadFile(authToken, hash, file, filename);
    }

}
