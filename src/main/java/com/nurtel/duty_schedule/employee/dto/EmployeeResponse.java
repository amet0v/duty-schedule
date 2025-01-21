package com.nurtel.duty_schedule.employee.dto;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class EmployeeResponse {
    protected Long id;
    protected String fullName;
    protected DepartmentEntity department;
    protected Boolean isManager;
    protected String group;
    protected String mainPhoneNumber;
    protected String alternativePhoneNumber;
    protected String telegram;
    protected EmployeeEntity ifUnavailable;
    protected Date lastCallDate;

    public static EmployeeResponse of(EmployeeEntity employee){
        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .department(employee.getDepartment())
                .isManager(employee.getIsManager())
                .group(employee.getGroup())
                .mainPhoneNumber(employee.getMainPhoneNumber())
                .alternativePhoneNumber(employee.getAlternativePhoneNumber())
                .telegram(employee.getTelegram())
                .ifUnavailable(employee.getIfUnavailable())
                .lastCallDate(employee.getLastCallDate())
                .build();
    }
}
