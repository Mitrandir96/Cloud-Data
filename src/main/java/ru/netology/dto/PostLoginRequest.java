package ru.netology.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PostLoginRequest {
    @NotBlank(message = "login can't be blank")
    @NotNull(message = "login can't be null")
    @NotEmpty(message = "login can't be empty")
    private String login;
    @NotBlank(message = "password can't be blank")
    @NotNull(message = "password can't be null")
    @NotEmpty(message = "password can't be empty")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
