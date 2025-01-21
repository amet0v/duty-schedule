package com.nurtel.duty_schedule.department.dto.response;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentShortResponse {
    protected Long id;
    protected String name;

    public static DepartmentShortResponse of(DepartmentEntity department){
        return DepartmentShortResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }
}
