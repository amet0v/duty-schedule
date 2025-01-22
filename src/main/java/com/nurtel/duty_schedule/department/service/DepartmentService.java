package com.nurtel.duty_schedule.department.service;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    public static List<DepartmentEntity> fillDepartments() {
        List<DepartmentEntity> departmentEntityList = new ArrayList<>();
        String[] departments = {"VAS", "Sys_Admin", "Developer", "Billing", "DBA", "QA"};
        for (String n : departments) {
            departmentEntityList.add(DepartmentEntity.builder()
                    .name(n)
                    .build()
            );
        }
        return departmentEntityList;
    }
}
