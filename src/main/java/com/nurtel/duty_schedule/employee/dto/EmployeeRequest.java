package com.nurtel.duty_schedule.employee.dto;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class EmployeeRequest {
    protected String fullName;
    protected DepartmentEntity department;
    protected Boolean isManager;
    protected String group;
    protected String mainPhoneNumber;
    protected String alternativePhoneNumber;
    protected String telegram;
    protected EmployeeEntity ifUnavailable;
//    protected Date lastCallDate;

    public void validate() throws BadRequestException {
        if (fullName == null || fullName.isBlank()) throw new BadRequestException();
        if (department == null) throw new BadRequestException();
    }
}
