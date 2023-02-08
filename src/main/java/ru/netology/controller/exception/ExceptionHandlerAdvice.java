package ru.netology.controller.exception;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.dto.GeneralErrorResponse;

import javax.security.auth.login.LoginException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> loginError (LoginException e) {
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage("Пользователь не найден.");
        // Указание идентификатора ошибки в моей системе, в данном случае 1 - loginException.
        err.setId(1);
        var ex = gson.toJson(err);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex);
    }

}
