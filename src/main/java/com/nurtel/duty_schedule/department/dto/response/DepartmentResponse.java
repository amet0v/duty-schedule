package com.nurtel.duty_schedule.department.dto.response;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.employee.dto.response.EmployeeShortResponse;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class DepartmentResponse {
    protected Long id;
    protected String name;
    protected List<EmployeeShortResponse> employees;

    public static DepartmentResponse of(DepartmentEntity department){
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .employees(department.getEmployees().stream().map(EmployeeShortResponse::of).collect(Collectors.toList()))
                .build();
    }
}
