package com.nurtel.duty_schedule.user.dto.request;

import com.nurtel.duty_schedule.exceptions.BadRequestException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequest {
    public String username;
    public String password;

    public void validate() throws BadRequestException {
        if (username == null || username.isBlank()) throw new BadRequestException();
        if (password == null || password.isBlank()) throw new BadRequestException();
    }
}
