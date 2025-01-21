package com.nurtel.duty_schedule.employee.dto.response;

import com.nurtel.duty_schedule.department.dto.response.DepartmentShortResponse;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class EmployeeShortResponse {
    protected Long id;
    protected String fullName;
    protected DepartmentShortResponse department;
    protected Boolean isManager;
    protected String group;
    protected String mainPhoneNumber;
    protected String alternativePhoneNumber;
    protected String telegram;
    protected Date lastCallDate;

    public static EmployeeShortResponse of(EmployeeEntity employee){
        return EmployeeShortResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .department(DepartmentShortResponse.of(employee.getDepartment()))
                .isManager(employee.getIsManager())
                .group(employee.getGroup())
                .mainPhoneNumber(employee.getMainPhoneNumber())
                .alternativePhoneNumber(employee.getAlternativePhoneNumber())
                .telegram(employee.getTelegram())
                .lastCallDate(employee.getLastCallDate())
                .build();
    }
}
