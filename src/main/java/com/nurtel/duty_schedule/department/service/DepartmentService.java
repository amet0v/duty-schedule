package com.nurtel.duty_schedule.department.service;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public static DepartmentEntity createDepartment(
            DepartmentRepository departmentRepository,
            String name
    ) throws BadRequestException {
        Optional<DepartmentEntity> departmentEntityOptional = departmentRepository.findByName(name);
        if (departmentEntityOptional.isPresent()) throw new BadRequestException("Отдел с таким названием уже существует");

        DepartmentEntity department = DepartmentEntity.builder()
                .name(name)
                .build();
        department = departmentRepository.save(department);
        return department;
    }

    public static DepartmentEntity editDepartment(
            DepartmentRepository departmentRepository,
            Long id,
            String name
    ) throws NotFoundException, BadRequestException {
        DepartmentEntity department;
        Optional<DepartmentEntity> departmentEntityOptional = departmentRepository.findById(id);
        if (departmentEntityOptional.isEmpty()) throw new NotFoundException("Отдела с указанным id не найден");
        else {
            department = departmentEntityOptional.get();
            departmentEntityOptional = departmentRepository.findByName(name);
            if (departmentEntityOptional.isPresent()) throw new BadRequestException("Отдел с таким названием уже существует");
            if (name != null) department.setName(name);
            department = departmentRepository.save(department);
        }
        return department;
    }

    public static void deleteDepartment(
            DepartmentRepository departmentRepository,
            Long id
    ) throws NotFoundException, BadRequestException {
        Optional<DepartmentEntity> department = departmentRepository.findById(id);
        if (department.isEmpty()) throw new NotFoundException("Отдела с указанным id не найден");
        if (department.get().getEmployees().isEmpty()) departmentRepository.deleteById(id);
        else throw new BadRequestException("");
    }
}
