package com.nurtel.duty_schedule.user.dto.response;

import com.nurtel.duty_schedule.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;

    public static UserResponse of(UserEntity user){
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
