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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return DepartmentResponse.of(departmentRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @PostMapping(BaseRoutes.DEPARTMENTS)
    public DepartmentResponse createDepartment(@RequestBody DepartmentRequest request) {
        DepartmentEntity department = DepartmentEntity.builder()
                .name(request.getName())
                .build();

        department = departmentRepository.save(department);
        return DepartmentResponse.of(department);
    }

    @PutMapping(BaseRoutes.DEPARTMENT_BY_ID)
    public DepartmentResponse editDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request)
            throws BadRequestException, NotFoundException {
        request.validate();
        DepartmentEntity department = departmentRepository.findById(id).orElseThrow(NotFoundException::new);

        department.setName(request.getName());

        department = departmentRepository.save(department);
        return DepartmentResponse.of(department);
    }

    @DeleteMapping(BaseRoutes.DEPARTMENT_BY_ID)
    public String deleteDepartment(@PathVariable Long id) throws NotFoundException, BadRequestException {
        DepartmentEntity department = departmentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (department.getEmployees().isEmpty()) departmentRepository.deleteById(id);
        else throw new BadRequestException();
        return HttpStatus.OK.name();
    }

}
