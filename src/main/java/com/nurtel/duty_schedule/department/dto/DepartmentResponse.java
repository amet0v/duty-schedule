package com.nurtel.duty_schedule.department.dto;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DepartmentResponse {
    protected Long id;
    protected String name;
    protected List<EmployeeEntity> employees;

    public static DepartmentResponse of(DepartmentEntity department){
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .employees(department.getEmployees())
                .build();
    }
}
