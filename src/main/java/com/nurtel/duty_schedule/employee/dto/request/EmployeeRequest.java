package com.nurtel.duty_schedule.employee.dto.request;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import lombok.Builder;
import lombok.Getter;

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
        if (isManager == null) isManager = false;
    }
}
