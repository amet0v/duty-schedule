package com.nurtel.duty_schedule.user.dto.request;

import com.nurtel.duty_schedule.exceptions.BadRequestException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequest {
    private String username;
    private String password;

    public void validate() throws BadRequestException {
        if (username == null || username.isBlank()) throw new BadRequestException("Введите логин для пользователя");
        if (password == null || password.isBlank()) throw new BadRequestException("Введите пароль для пользователя");
    }
}
