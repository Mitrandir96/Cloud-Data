package ru.netology.unitTests;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.dto.GetListResponse;
import ru.netology.dto.GetListResponseItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        assertSame(expected.getClass(), actual.getClass());
        assertEquals(expected.getAuthToken(), actual.getAuthToken());
    }

    @Test
    public void login_notExistingUser_throwsLoginException_Test() {
        var login = "notExistingUser";
        var password = "password";
        Optional<User> optionalUser = Optional.empty();
        var userRepository = Mockito.mock(UserRepository.class);
        var userService = new UserService(userRepository);

        Mockito.when(userRepository.findByLoginAndPasswordHash(login, password)).thenReturn(optionalUser);

        assertThrows(LoginException.class, () -> userService.login(login, password));
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
        Mockito.verify(userRepository, Mockito.times(1)).saveAndFlush(user);
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
        Mockito.verify(userRepository, never()).saveAndFlush(user);
    }

    @Test
    public void uploadFile_validArguments_Test() throws AuthException, IOException {
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
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, Mockito.times(1)).saveAndFlush(Mockito.notNull());
    }

    @Test
    public void uploadFile_notExistingUser_throwsAuthException_Test() {
        var authToken = "auth-token";
        var filename = "filename";
        var hash = "hash";
        var file = Mockito.mock(MultipartFile.class);
        Optional<User> optionalUser = Optional.empty();
        var user = Mockito.mock(User.class);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        assertThrows(AuthException.class, () -> fileService.uploadFile(authToken, hash, file, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).saveAndFlush(Mockito.notNull());
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

        assertThrows(IllegalArgumentException.class, () -> fileService.uploadFile(authToken, hash, file, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).saveAndFlush(Mockito.notNull());
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

        assertThrows(IOException.class, () -> fileService.uploadFile(authToken, hash, file, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).saveAndFlush(Mockito.notNull());
    }

    @Test
    public void uploadFile_withSameName_throwsIllegalException_Test() {
        var authToken = "auth-token";
        var filename = "sameFilename";
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

        assertThrows(IllegalArgumentException.class, () -> fileService.uploadFile(authToken, hash, currentFile, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).saveAndFlush(file);
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
    public void deleteFile_notExistingFile_throwsNoSuchElementException_Test() {
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

        assertThrows(NoSuchElementException.class, () -> fileService.deleteFile(authToken, filename));
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

        assertThrows(AuthException.class, () -> fileService.deleteFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(Mockito.notNull(), Mockito.notNull());
        Mockito.verify(fileRepository, never()).delete(Mockito.notNull());
    }

    @Test
    public void deleteFile_emptyFilename_throwsIllegalException_Test() {
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

        assertThrows(IllegalArgumentException.class, () -> fileService.deleteFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).delete(Mockito.notNull());
    }

    @Test
    public void getFile_existingFile_returnsHashAndСontent_Test() throws AuthException {
        var authToken = "auth-token";
        var filename = "existingFilename";
        var user = new User();
        var file = new File();
        var optionalFile = Optional.of(file);
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);
        var formData = new LinkedMultiValueMap<String, Object>();
        var currentFile = optionalFile.get();
        formData.add("hash", currentFile.getHash());
        formData.add("file", currentFile.getContent());

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        var expected = ResponseEntity.status(HttpStatus.OK).contentType(MediaType.MULTIPART_FORM_DATA).body(formData);
        var actual = fileService.getFile(authToken, filename);

        assertEquals(expected, actual);
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
    }

    @Test
    public void getFile_notExistingUser_throwsAuthException_Test() {
        var authToken = "auth-token";
        var filename = "filename";
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);
        Optional<User> optionalUser = Optional.empty();

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        assertThrows(AuthException.class, () -> fileService.getFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(Mockito.notNull(), Mockito.notNull());
    }

    @Test
    public void getFile_emptyFilename_throwsIllegalException_Test() {
        var authToken = "auth-token";
        var filename = "";
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);
        var user = new User();
        var optionalUser = Optional.of(user);
        Optional<File> optionalFile = Optional.empty();

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        assertThrows(IllegalArgumentException.class, () -> fileService.getFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(filename, user);
    }

    @Test
    public void getFile_notExistingFile_throwsNoSuchElementException_Test() {
        var authToken = "auth-token";
        var filename = "filename";
        var user = new User();
        Optional<File> optionalFile = Optional.empty();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        assertThrows(NoSuchElementException.class, () -> fileService.getFile(authToken, filename));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
    }

    @Test
    public void renameFile_existingFile_returns_Test() throws AuthException {
        var authToken = "auth-token";
        var filename = "existingFilename";
        var name = "newName";
        var user = new User();
        var file = new File();
        file.setName(filename);
        var optionalFile = Optional.of(file);
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        fileService.renameFile(authToken, filename, name);

        assertEquals(name, file.getName());
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, Mockito.times(1)).saveAndFlush(file);
    }

    @Test
    public void renameFile_notExistingFile_throwsNoSuchElementException_Test() {
        var authToken = "auth-token";
        var filename = "filename";
        var name = "newName";
        var user = new User();
        Optional<File> optionalFile = Optional.empty();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        assertThrows(NoSuchElementException.class, () -> fileService.renameFile(authToken, filename, name));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).saveAndFlush(Mockito.notNull());
    }

    @Test
    public void renameFile_notExistingUser_throwsAuthException_Test() {
        var authToken = "auth-token";
        var filename = "filename";
        var name = "newName";
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);
        Optional<User> optionalUser = Optional.empty();

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        Assert.assertThrows(AuthException.class, () -> fileService.renameFile(authToken, filename, name));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(Mockito.notNull(), Mockito.notNull());
        Mockito.verify(fileRepository, never()).saveAndFlush(Mockito.notNull());
    }

    @Test
    public void renameFile_emptyFileName_throwsIllegalArgumentException_Test() {
        var authToken = "auth-token";
        var filename = "";
        var name = "newName";
        var user = new User();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        assertThrows(IllegalArgumentException.class, () -> fileService.renameFile(authToken, filename, name));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findFileByNameAndUser(filename, user);
        Mockito.verify(fileRepository, never()).saveAndFlush(Mockito.notNull());
    }

    @Test
    public void getList_validArguments_returnsResponse_Test() throws AuthException {
        var authToken = "auth-token";
        var limit = 4;
        var user = new User();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);
        List<File> files = new ArrayList<>();
        List<GetListResponseItem> list = new ArrayList<>();
        var response = new GetListResponse();
        response.setFiles(list);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findAllByUser(user, PageRequest.of(0, limit))).thenReturn(files);

        var expected = response;
        var actual = fileService.getList(authToken, limit);

        assertSame(expected.getClass(), actual.getClass());
        assertEquals(expected.getFiles(), actual.getFiles());
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, Mockito.times(1)).findAllByUser(user, PageRequest.of(0, limit));
    }

    @Test
    public void getList_notExistingUser_throwsAuthException_Test() {
        var authToken = "auth-token";
        var limit = 4;
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);
        Optional<User> optionalUser = Optional.empty();

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        assertThrows(AuthException.class, () -> fileService.getList(authToken, limit));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findAllByUser(Mockito.notNull(), Mockito.notNull());
    }

    @Test
    public void getList_notValidLimit_throwsIllegalException_Test() {
        var authToken = "auth-token";
        var limit = 0;
        var user = new User();
        var optionalUser = Optional.of(user);
        var fileRepository = Mockito.mock(FileRepository.class);
        var userRepository = Mockito.mock(UserRepository.class);
        var fileService = new FileService(fileRepository, userRepository);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);

        assertThrows(IllegalArgumentException.class, () -> fileService.getList(authToken, limit));
        Mockito.verify(userRepository, Mockito.times(1)).findUserByAuthToken(authToken);
        Mockito.verify(fileRepository, never()).findAllByUser(Mockito.notNull(), Mockito.notNull());
    }
}
