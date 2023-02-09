package ru.netology.controller.exception;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.dto.GeneralErrorResponse;

import javax.security.auth.login.LoginException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> onLoginError (LoginException e) {
        return prepareBadRequestResponseEntity(e.getMessage(), 1);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> onValidationError(MethodArgumentNotValidException e) {
        return prepareBadRequestResponseEntity(e.getFieldError().getDefaultMessage(), 2);
    }

    private ResponseEntity<String> prepareBadRequestResponseEntity(String message, int id) {
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(message);
        // Указание идентификатора ошибки в моей системе.
        err.setId(id);
        var ex = gson.toJson(err);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex);

    }

}
