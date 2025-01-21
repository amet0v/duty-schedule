package com.nurtel.duty_schedule.schedule.dto.response;

import com.nurtel.duty_schedule.employee.dto.response.EmployeeResponse;
import com.nurtel.duty_schedule.employee.dto.response.EmployeeShortResponse;
import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ScheduleResponse {
    protected Long id;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected EmployeeResponse employee;
    protected EventTypes event;

    public static ScheduleResponse of(ScheduleEntity schedule){
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .employee(EmployeeResponse.of(schedule.getEmployee()))
                .event(schedule.getEvent())
                .build();
    }
}
