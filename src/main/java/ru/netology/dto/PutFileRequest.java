package ru.netology.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PutFileRequest {
    @NotEmpty(message = "name can't be empty")
    @NotBlank(message = "name can't be blank")
    @NotNull(message = "name can't be null")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
