package ru.netology.unitTests;

import com.google.gson.Gson;
import org.apache.commons.compress.utils.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.netology.controller.Controller;
import ru.netology.controller.exception.ExceptionHandlerAdvice;
import ru.netology.dto.GeneralErrorResponse;
import ru.netology.dto.PostLoginRequest;
import ru.netology.dto.PostLoginResponse;
import ru.netology.service.UserService;

import javax.security.auth.login.LoginException;

public class ControllerTests {
    @Test
    public void login_ExistingUsers_returnsAuthTokenWith200() throws LoginException {
        var postLoginResponse = new PostLoginResponse();
        var postLoginRequest = new PostLoginRequest();
        postLoginResponse.setAuthToken("auth-token");
        postLoginRequest.setLogin("existingUser");
        postLoginRequest.setPassword("password");
        var service = Mockito.mock(UserService.class);
        var controller = new Controller(service);

        Mockito.when(service.login(postLoginRequest.getLogin(), postLoginRequest.getPassword())).thenReturn(postLoginResponse);

        var expected = postLoginResponse;
        var actual = controller.login(postLoginRequest);

        Assert.assertEquals(expected, actual);
        Mockito.verify(service, Mockito.times(1)).login(postLoginRequest.getLogin(), postLoginRequest.getPassword());
    }

    @Test
    public void onLoginError_returnsErrorIdAndMessageWith400() {
        var exception = new LoginException("login and/or password is incorrect");
        var exceptionHandler = new ExceptionHandlerAdvice();
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(exception.getMessage());
        err.setId(1);
        var ex = gson.toJson(err);

        var expected = ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex);
        var actual = exceptionHandler.onLoginError(exception);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void onValidationError_returnsErrorIdAndMessageWith400() {
        var exception = Mockito.mock(MethodArgumentNotValidException.class);
        var fieldError = Mockito.mock(FieldError.class);
        var exceptionMessage = "login can't be empty";
        var exceptionHandler = new ExceptionHandlerAdvice();
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(exceptionMessage);
        err.setId(2);
        var ex = gson.toJson(err);

        Mockito.when(exception.getFieldError()).thenReturn(fieldError);
        Mockito.when(fieldError.getDefaultMessage()).thenReturn(exceptionMessage);

        var expected = ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex);
        var actual = exceptionHandler.onValidationError(exception);

        Assert.assertEquals(expected, actual);
    }







}
