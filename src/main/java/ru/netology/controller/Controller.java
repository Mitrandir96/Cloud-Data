package ru.netology.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.GetListResponse;
import ru.netology.dto.PostLoginRequest;
import ru.netology.dto.PostLoginResponse;
import ru.netology.dto.PutFileRequest;
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
    public PostLoginResponse login(@RequestBody PostLoginRequest request) throws LoginException {
        return userService.login(request.getLogin(), request.getPassword());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("auth-token") String authToken) {
        userService.logout(authToken);
    }

    @PostMapping("/file")
    public void uploadFile(@RequestHeader("auth-token") String authToken, @RequestPart String hash, @RequestPart MultipartFile file, @RequestParam String filename) throws AuthException, IOException {
        fileService.uploadFile(authToken, hash, file, filename);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename) throws AuthException {
        fileService.deleteFile(authToken, filename);
    }

    @GetMapping("/file")
    public ResponseEntity<MultiValueMap<String, Object>> getFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename) throws AuthException {
        return fileService.getFile(authToken, filename);
    }

    @PutMapping("/file")
    public void putFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename, @Validated @RequestBody PutFileRequest putFileRequest) throws AuthException {
        fileService.renameFile(authToken, filename, putFileRequest.getName());
    }

    @GetMapping("/list")
    public GetListResponse getList(@RequestHeader("auth-token") String authToken, @RequestParam Integer limit) throws AuthException {
        return fileService.getList(authToken, limit);
    }

}
