package com.nurtel.duty_schedule.employee.controller;

import com.nurtel.duty_schedule.employee.dto.request.EmployeeRequest;
import com.nurtel.duty_schedule.employee.dto.response.EmployeeResponse;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.routes.BaseRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeRepository employeeRepository;

    @GetMapping(BaseRoutes.EMPLOYEE_BY_ID)
    public EmployeeResponse getEmployee(@PathVariable Long id) throws NotFoundException {
        return EmployeeResponse.of(employeeRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @PostMapping(BaseRoutes.EMPLOYEE)
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest request) throws BadRequestException {
        request.validate();
        EmployeeEntity employee = EmployeeEntity.builder()
                .fullName(request.getFullName())
                .department(request.getDepartment())
                .isManager(request.getIsManager())
                .group(request.getGroup())
                .mainPhoneNumber(request.getMainPhoneNumber())
                .alternativePhoneNumber(request.getAlternativePhoneNumber())
                .telegram(request.getTelegram())
                .ifUnavailable(request.getIfUnavailable())
                .build();

        employee = employeeRepository.save(employee);
        return EmployeeResponse.of(employee);
    }

    @PutMapping(BaseRoutes.EMPLOYEE_BY_ID)
    public EmployeeResponse editEmployee(@PathVariable Long id, @RequestBody EmployeeRequest request)
            throws BadRequestException, NotFoundException {
        EmployeeEntity employee = employeeRepository.findById(id).orElseThrow(NotFoundException::new);

        if(request.getFullName() != null) employee.setFullName(request.getFullName());
        if(request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if(request.getIsManager() != null) employee.setIsManager(request.getIsManager());
        if(request.getGroup() != null) employee.setGroup(request.getGroup());
        if(request.getMainPhoneNumber() != null) employee.setMainPhoneNumber(request.getMainPhoneNumber());
        if(request.getAlternativePhoneNumber() != null) employee.setAlternativePhoneNumber(request.getAlternativePhoneNumber());
        if(request.getTelegram() != null) employee.setTelegram(request.getTelegram());
        if(request.getIfUnavailable() != null) employee.setIfUnavailable(request.getIfUnavailable());

        employee = employeeRepository.save(employee);
        return EmployeeResponse.of(employee);
    }

    @DeleteMapping(BaseRoutes.EMPLOYEE_BY_ID)
    public String deleteEmployee(@PathVariable Long id) throws NotFoundException {
        //EmployeeEntity employee = employeeRepository.findById(id).orElseThrow(NotFoundException::new);
        employeeRepository.deleteById(id);
        return HttpStatus.OK.name();
    }
}
