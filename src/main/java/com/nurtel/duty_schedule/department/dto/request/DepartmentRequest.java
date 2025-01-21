package com.nurtel.duty_schedule.department.dto.request;

import com.nurtel.duty_schedule.exceptions.BadRequestException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentRequest {
    protected String name;

    public void validate() throws BadRequestException {
        if (name == null || name.isBlank()) throw new BadRequestException();
    }
}
