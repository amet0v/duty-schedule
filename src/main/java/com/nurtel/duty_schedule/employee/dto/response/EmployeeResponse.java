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
    protected EmployeeShortResponse manager;
    protected Date lastCallDate;

    public static EmployeeResponse of(EmployeeEntity employee){
        if (employee == null) return null;

        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .department(DepartmentShortResponse.of(employee.getDepartment()))
                .isManager(employee.getIsManager())
                .group(employee.getGroup())
                .mainPhoneNumber(employee.getMainPhoneNumber())
                .alternativePhoneNumber(employee.getAlternativePhoneNumber())
                .telegram(employee.getTelegram())
                .ifUnavailable(employee.getIfUnavailable() != null ? EmployeeShortResponse.of(employee.getIfUnavailable()) : null)
                .manager(employee.getManager() != null ? EmployeeShortResponse.of(employee.getManager()) : null)
                .lastCallDate(employee.getLastCallDate())
                .build();
    }
}
