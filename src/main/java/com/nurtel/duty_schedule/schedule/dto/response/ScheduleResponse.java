package com.nurtel.duty_schedule.schedule.dto.response;

import com.nurtel.duty_schedule.employee.dto.response.EmployeeShortResponse;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ScheduleResponse {
    protected Long id;
    protected Date startDate;
    protected Date endDate;
    protected EmployeeShortResponse employee;
    protected EventTypes event;

    public static ScheduleResponse of(ScheduleEntity schedule){
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .employee(EmployeeShortResponse.of(schedule.getEmployee()))
                .event(schedule.getEvent())
                .build();
    }
}
