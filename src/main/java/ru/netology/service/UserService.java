package ru.netology.service;

import org.springframework.stereotype.Service;
import ru.netology.repositories.UserRepository;

import javax.security.auth.login.LoginException;
@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(String login, String password) throws LoginException {
        var optionalUser = userRepository.findByLoginAndPasswordHash(login, password);
        if (!optionalUser.isPresent()) {
            throw new LoginException("Login and/or password is incorrect.");
        }
        var user = optionalUser.get();
        user.setAuthToken("123");
        return user.getAuthToken();
    }
}
