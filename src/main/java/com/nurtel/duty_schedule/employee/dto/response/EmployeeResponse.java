package com.nurtel.duty_schedule.employee.dto.response;

import com.nurtel.duty_schedule.department.dto.response.DepartmentResponse;
import com.nurtel.duty_schedule.department.dto.response.DepartmentShortResponse;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
public class EmployeeResponse {
    protected Long id;
    protected String fullName;
    protected DepartmentShortResponse department;
    protected Boolean isManager;
    protected String group;
    protected String mainPhoneNumber;
    protected String alternativePhoneNumber;
    protected String telegram;
    protected EmployeeShortResponse ifUnavailable;
    protected Date lastCallDate;

    public static EmployeeResponse of(EmployeeEntity employee){
        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .department(DepartmentShortResponse.of(employee.getDepartment()))
                .isManager(employee.getIsManager())
                .group(employee.getGroup())
                .mainPhoneNumber(employee.getMainPhoneNumber())
                .alternativePhoneNumber(employee.getAlternativePhoneNumber())
                .telegram(employee.getTelegram())
                .ifUnavailable(EmployeeShortResponse.of(employee.getIfUnavailable()))
                .lastCallDate(employee.getLastCallDate())
                .build();
    }
}
