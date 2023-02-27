package ru.netology.service;

import org.springframework.stereotype.Service;
import ru.netology.dto.PostLoginResponse;
import ru.netology.repositories.UserRepository;

import javax.security.auth.login.LoginException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PostLoginResponse login(String login, String password) throws LoginException {
        var optionalUser = userRepository.findByLoginAndPasswordHash(login, password);
        if (optionalUser.isEmpty()) {
            throw new LoginException("login and/or password is incorrect");
        }
        var user = optionalUser.get();
        user.setAuthToken("123");
        userRepository.save(user);
        var postLoginResponse = new PostLoginResponse();
        postLoginResponse.setAuthToken(user.getAuthToken());
        return postLoginResponse;
    }

    public void logout(String authToken) {
        var optionalUser = userRepository.findUserByAuthToken(authToken);
        if (optionalUser.isEmpty()) {
            return;
        }
        var user = optionalUser.get();
        user.setAuthToken(null);
        userRepository.saveAndFlush(user);
    }
}
