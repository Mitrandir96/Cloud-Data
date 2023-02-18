package ru.netology.unitTests;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import ru.netology.dto.PostLoginResponse;
import ru.netology.entities.File;
import ru.netology.entities.User;
import ru.netology.repositories.FileRepository;
import ru.netology.repositories.UserRepository;
import ru.netology.service.FileService;
import ru.netology.service.UserService;

import javax.security.auth.login.LoginException;
import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.never;

public class ServiceTests {
    @Test
    public void login_existingUser_returnsPostLoginResponseWithAuthToken_Test() throws LoginException {
        var login = "existingUser";
        var password = "password";
        var authToken = "123";
        var user = new User();
        var postLoginResponse = new PostLoginResponse();
        postLoginResponse.setAuthToken(authToken);
        user.setAuthToken(authToken);
        user.setLogin(login);
        user.setPasswordHash(password);
        var optionalUser = Optional.of(user);
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        Mockito.when(userRepository.findByLoginAndPasswordHash(login, password)).thenReturn(optionalUser);

        var expected = postLoginResponse;
        var actual = userService.login(login, password);

        Assert.assertSame(expected.getClass(), actual.getClass());
        Assert.assertEquals(expected.getAuthToken(), actual.getAuthToken());
    }

    @Test
    public void login_notExistingUser_throwsLoginException_Test() throws LoginException {
        var login = "notExistingUser";
        var password = "password";
        Optional<User> optionalUser = Optional.empty();
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        Mockito.when(userRepository.findByLoginAndPasswordHash(login, password)).thenReturn(optionalUser);

        Assert.assertThrows(LoginException.class, () -> userService.login(login, password));
    }

    @Test
    public void logout_existingUser_Test() {
        var authToken = "auth-token";
        var user = new User();
        var optionalUser = Optional.of(user);
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        userService.logout(authToken);

        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void logout_notExistingUser_Test() {
        var authToken = "auth-token";
        var user = new User();
        Optional<User> optionalUser = Optional.empty();
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        userService.logout(authToken);

        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(userRepository, never()).save(user);
    }

    @Test
    public void uploadFile_existingUser_Test() throws AuthException, IOException {
        var authToken = "auth-token";
        var filename = "filename";
        var hash = "hash";
        var file = Mockito.mock(MultipartFile.class);
        var user = new User();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        fileService.uploadFile(authToken, hash, file, filename);

        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).save(Mockito.notNull());
    }

    @Test
    public void uploadFile_notExistingUser_throwsAuthException_Test() {
        var authToken = "auth-token";
        var filename = "filename";
        var hash = "hash";
        var file = Mockito.mock(MultipartFile.class);
        Optional<User> optionalUser = Optional.empty();
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        Assert.assertThrows(AuthException.class, () -> fileService.uploadFile(authToken, hash, file, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).save(Mockito.notNull());
    }

    @Test
    public void uploadFile_emptyFilename_throwsIllegalException_Test() {
        var authToken = "auth-token";
        var filename = "";
        var hash = "hash";
        var file = Mockito.mock(MultipartFile.class);
        var user = new User();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        Assert.assertThrows(IllegalArgumentException.class, () -> fileService.uploadFile(authToken, hash, file, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).save(Mockito.notNull());
    }

    @Test
    public void uploadFile_emptyByteArray_throwsIOException_Test() throws IOException {
        var authToken = "auth-token";
        var filename = "filename";
        var hash = "hash";
        var file = Mockito.mock(MultipartFile.class);
        var user = new User();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(file.getBytes()).thenThrow(new IOException());
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        Assert.assertThrows(IOException.class, () -> fileService.uploadFile(authToken, hash, file, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).save(Mockito.notNull());
    }

    @Test
    public void uploadFile_WithExistingName_throwsIllegalException_Test() {
        var authToken = "auth-token";
        var filename = "existingFilename";
        var hash = "hash";
        var currentFile = Mockito.mock(MultipartFile.class);
        var user = new User();
        var file = new File();
        var optionalFile = Optional.of(file);
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        Assert.assertThrows(IllegalArgumentException.class, () -> fileService.uploadFile(authToken, hash, currentFile, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).save(Mockito.notNull());
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
    }

    @Test
    public void deleteFile_existingFile_Test() throws AuthException {
        var authToken = "auth-token";
        var filename = "existingFilename";
        var user = new User();
        var file = new File();
        var optionalFile = Optional.of(file);
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        fileService.deleteFile(authToken, filename);

        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, Mockito.times(1)).delete(file);
    }

    @Test
    public void deleteFile_notExistingFile_throwsIllegalException_Test() {
        var authToken = "auth-token";
        var filename = "existingFilename";
        var user = new User();
        Optional<File> optionalFile = Optional.empty();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        Assert.assertThrows(IllegalArgumentException.class, () -> fileService.deleteFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).delete(Mockito.notNull());
    }

    @Test
    public void deleteFile_notExistingUser_throwsAuthException_Test() {
        var authToken = "auth-token";
        var filename = "existingFilename";
        Optional<User> optionalUser = Optional.empty();
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        Assert.assertThrows(AuthException.class, () -> fileService.deleteFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(Mockito.notNull(), Mockito.notNull());
        Mockito.verify(fileRepository, never()).delete(Mockito.notNull());
    }

    @Test
    public void deleteFile_EmptyFilename_throwsIllegalException_Test() {
        var authToken = "auth-token";
        var filename = "";
        var user = new User();
        Optional<File> optionalFile = Optional.empty();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        Assert.assertThrows(IllegalArgumentException.class, () -> fileService.deleteFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).delete(Mockito.notNull());
    }

}
