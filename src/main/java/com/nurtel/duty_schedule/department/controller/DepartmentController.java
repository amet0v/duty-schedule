package com.nurtel.duty_schedule.department.controller;

import com.nurtel.duty_schedule.department.dto.request.DepartmentRequest;
import com.nurtel.duty_schedule.department.dto.response.DepartmentResponse;
import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.department.service.DepartmentService;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.routes.BaseRoutes;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.N;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;

    @PostMapping(BaseRoutes.NOT_SECURED_DEPARTMENT_FILL)
    public String fillDepartments(){
        if (departmentRepository.count() == 0) departmentRepository.saveAll(DepartmentService.fillDepartments());
        return HttpStatus.OK.name();
    }

    @GetMapping(BaseRoutes.DEPARTMENTS)
    public List<DepartmentResponse> getDepartments() {
        return departmentRepository.findAll().stream().map(DepartmentResponse::of).collect(Collectors.toList());
    }

    @GetMapping(BaseRoutes.DEPARTMENT_BY_ID)
    public DepartmentResponse getDepartment(@PathVariable Long id) throws NotFoundException {
        Optional<DepartmentEntity> department = departmentRepository.findById(id);
        if (department.isEmpty()) throw new NotFoundException("Отдел с указанным id не найден");
        return DepartmentResponse.of(department.get());
    }

    @PostMapping(BaseRoutes.DEPARTMENTS)
    public DepartmentResponse createDepartment(@RequestBody DepartmentRequest request) throws BadRequestException {
        return DepartmentResponse.of(DepartmentService.createDepartment(departmentRepository, request.getName()));
    }

    @PutMapping(BaseRoutes.DEPARTMENT_BY_ID)
    public DepartmentResponse editDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request)
            throws BadRequestException, NotFoundException {
        request.validate();
        return DepartmentResponse.of(DepartmentService.editDepartment(departmentRepository, id, request.getName()));
    }

    @DeleteMapping(BaseRoutes.DEPARTMENT_BY_ID)
    public String deleteDepartment(@PathVariable Long id) throws NotFoundException, BadRequestException {
        DepartmentService.deleteDepartment(departmentRepository, id);
        return HttpStatus.OK.name();
    }

}
