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
import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> onLoginError (LoginException e) {
        return prepareResponseEntity(e.getMessage(), 1, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> onLoginValidationError(MethodArgumentNotValidException e) {
        return prepareResponseEntity(e.getFieldError().getDefaultMessage(), 2, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> authenticationError(AuthException e) {
        return prepareResponseEntity(e.getMessage(), 3, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> onFileValidationError(IllegalArgumentException e) {
        return prepareResponseEntity(e.getMessage(), 4, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> getFileBytesError(IOException e) {
        return prepareResponseEntity(e.getMessage(), 5, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> getFileFromRepository(NoSuchElementException e) {
        return prepareResponseEntity(e.getMessage(), 6, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> prepareResponseEntity(String message, int id, HttpStatus status) {
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(message);
        // Указание идентификатора ошибки в моей системе.
        err.setId(id);
        var ex = gson.toJson(err);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(ex);
    }

}
