package com.nurtel.duty_schedule.schedule.dto.request;

import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
public class ScheduleRequest {
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected EmployeeEntity employee;
    protected EventTypes event;

    public void validate() throws BadRequestException {
        if (startDate == null) throw new BadRequestException("Необходимо указать дату начала");
        if (employee == null) throw new BadRequestException("Необходимо указать сотрудника");

        if(endDate != null && startDate.isAfter(endDate)) throw new BadRequestException("Дата окончания не может быть раньше даты начала");

        if (endDate == null) endDate = startDate;
        //if (event == EventTypes.Duty && (startDate != endDate)) throw new BadRequestException();
    }
}
