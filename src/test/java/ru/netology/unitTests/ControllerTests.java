package ru.netology.unitTests;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.controller.Controller;
import ru.netology.controller.exception.ExceptionHandlerAdvice;
import ru.netology.dto.GeneralErrorResponse;
import ru.netology.dto.PostLoginRequest;
import ru.netology.dto.PostLoginResponse;
import ru.netology.dto.PutFileRequest;
import ru.netology.service.FileService;
import ru.netology.service.UserService;

import javax.security.auth.login.LoginException;
import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.util.NoSuchElementException;

public class ControllerTests {
    @Test
    public void login_existingUser_returnsAuthTokenWith200_Test() throws LoginException {
        var postLoginResponse = new PostLoginResponse();
        var postLoginRequest = new PostLoginRequest();
        postLoginResponse.setAuthToken("auth-token");
        postLoginRequest.setLogin("existingUser");
        postLoginRequest.setPassword("password");
        var userService = Mockito.mock(UserService.class);
        var controller = new Controller(userService, null);

        Mockito.when(userService.login(postLoginRequest.getLogin(), postLoginRequest.getPassword())).thenReturn(postLoginResponse);

        var expected = postLoginResponse;
        var actual = controller.login(postLoginRequest);

        Assert.assertEquals(expected, actual);
        Mockito.verify(userService, Mockito.times(1)).login(postLoginRequest.getLogin(), postLoginRequest.getPassword());
    }

    @Test
    public void onLoginError_returnsErrorIdAndMessageWith400_Test() {
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
    public void onValidationError_returnsErrorIdAndMessageWith400_Test() {
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
        var actual = exceptionHandler.onLoginValidationError(exception);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void logout_Test() {
        var authToken = "auth-token";
        var userService = Mockito.mock(UserService.class);
        var controller = new Controller(userService, null);

        controller.logout(authToken);

        Mockito.verify(userService, Mockito.times(1)).logout(authToken);
    }

    @Test
    public void uploadFile_Test() throws LoginException, IOException, IllegalArgumentException {
        var authToken = "auth-token";
        var filename = "filename";
        var hash = "hash";
        var fileService = Mockito.mock(FileService.class);
        var file = Mockito.mock(MultipartFile.class);
        var controller = new Controller(null, fileService);

        controller.uploadFile(authToken, hash, file, filename);

        Mockito.verify(fileService, Mockito.times(1)).uploadFile(authToken, hash, file, filename);
    }

    @Test
    public void authenticationError_returnsErrorIdAndMessageWith401_Test() {
        var exception = new AuthException("user with provided auth token not found");
        var status = HttpStatus.UNAUTHORIZED;
        var exceptionHandler = new ExceptionHandlerAdvice();
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(exception.getMessage());
        err.setId(3);
        var ex = gson.toJson(err);

        var expected = ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(ex);
        var actual = exceptionHandler.authenticationError(exception);

        Assert.assertEquals(expected, actual);
        Assert.assertSame(expected.getClass(), actual.getClass());
    }

    @Test
    public void onFileValidationError_returnsErrorIdAndMessageWith400_Test() {
        var exception = new IllegalArgumentException("filename can`t be empty");
        var status = HttpStatus.BAD_REQUEST;
        var exceptionHandler = new ExceptionHandlerAdvice();
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(exception.getMessage());
        err.setId(4);
        var ex = gson.toJson(err);

        var expected = ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(ex);
        var actual = exceptionHandler.onFileValidationError(exception);

        Assert.assertEquals(expected, actual);
        Assert.assertSame(expected.getClass(), actual.getClass());
    }

    @Test
    public void getFileBytesError_returnsErrorIdAndMessageWith400_Test() {
        var exception = new IOException("can`t get file bytes");
        var status = HttpStatus.BAD_REQUEST;
        var exceptionHandler = new ExceptionHandlerAdvice();
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(exception.getMessage());
        err.setId(5);
        var ex = gson.toJson(err);

        var expected = ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(ex);
        var actual = exceptionHandler.getFileBytesError(exception);

        Assert.assertEquals(expected, actual);
        Assert.assertSame(expected.getClass(), actual.getClass());
    }

    @Test
    public void deleteFile_Test() throws AuthException {
        var authToken = "auth-token";
        var filename = "filename";
        var fileService = Mockito.mock(FileService.class);
        var controller = new Controller(null, fileService);

        controller.deleteFile(authToken, filename);

        Mockito.verify(fileService, Mockito.times(1)).deleteFile(authToken, filename);
    }

    @Test
    public void getFileFromRepository_returnsErrorIdAndMessageWith500_Test() {
        var exception = new NoSuchElementException("file with provided filename not found");
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var exceptionHandler = new ExceptionHandlerAdvice();
        var gson = new Gson();
        var err = new GeneralErrorResponse();
        err.setMessage(exception.getMessage());
        err.setId(6);
        var ex = gson.toJson(err);

        var expected = ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(ex);
        var actual = exceptionHandler.getFileFromRepository(exception);

        Assert.assertEquals(expected, actual);
        Assert.assertSame(expected.getClass(), actual.getClass());
    }

    @Test
    public void getFile_existingFile_returnsHashAndContentWith200_Test() throws AuthException {
        var authToken = "auth-token";
        var filename = "existingFilename";
        var fileService = Mockito.mock(FileService.class);
        var controller = new Controller(null, fileService);
        var formData = new LinkedMultiValueMap<String, Object>();
        formData.add("hash", "123");
        formData.add("file", 123);

        Mockito.when(fileService.getFile(authToken, filename)).thenReturn(ResponseEntity.status(HttpStatus.OK).contentType(MediaType.MULTIPART_FORM_DATA).body(formData));

        var expected = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.MULTIPART_FORM_DATA).body(formData);
        var actual = controller.getFile(authToken, filename);

        Assert.assertEquals(expected, actual);
        Mockito.verify(fileService, Mockito.times(1)).getFile(authToken, filename);
    }

    @Test
    public void putFile_existingFile_Test() throws AuthException {
        var authToken = "auth-token";
        var filename = "existingFilename";
        var putFileRequest = new PutFileRequest();
        var name = "newName";
        putFileRequest.setName(name);
        var fileService = Mockito.mock(FileService.class);
        var controller = new Controller(null, fileService);

        controller.putFile(authToken, filename, putFileRequest);

        Mockito.verify(fileService, Mockito.times(1)).renameFile(authToken, filename, name);
    }


}
