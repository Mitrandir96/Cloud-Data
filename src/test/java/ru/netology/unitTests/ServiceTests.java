package ru.netology.unitTests;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.dto.PostLoginResponse;
import ru.netology.entities.User;
import ru.netology.repositories.UserRepository;
import ru.netology.service.UserService;

import javax.security.auth.login.LoginException;
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

        Assert.assertSame(actual.getClass(), PostLoginResponse.class);
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

}
