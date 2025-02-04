package com.nurtel.duty_schedule.employee.controller;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.dto.request.EmployeeRequest;
import com.nurtel.duty_schedule.employee.dto.response.EmployeeResponse;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.employee.service.EmployeeService;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.routes.BaseRoutes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import com.nurtel.duty_schedule.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ScheduleRepository scheduleRepository;

    @GetMapping(BaseRoutes.EMPLOYEE_BY_ID)
    public EmployeeResponse getEmployee(@PathVariable Long id) throws NotFoundException {
        Optional<EmployeeEntity> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) throw new NotFoundException("Сотрудник с указанным id не найден");
        return EmployeeResponse.of(employee.get());
    }

    @PostMapping(BaseRoutes.EMPLOYEES)
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest request) throws BadRequestException, NotFoundException {
        request.validate();

        EmployeeEntity employee = EmployeeService.createEmployee(
                departmentRepository,
                employeeRepository,
                request.getFullName(),
                request.getDepartment(),
                request.getIsManager(),
                request.getGroup(),
                request.getMainPhoneNumber(),
                request.getAlternativePhoneNumber(),
                request.getTelegram(),
                request.getIfUnavailable()
        );
        return EmployeeResponse.of(employee);
    }

    @PutMapping(BaseRoutes.EMPLOYEE_BY_ID)
    public EmployeeResponse editEmployee(@PathVariable Long id, @RequestBody EmployeeRequest request)
            throws BadRequestException, NotFoundException {
        request.validate();

        EmployeeEntity employee = EmployeeService.editEmployee(
                departmentRepository,
                employeeRepository,
                id,
                request.getFullName(),
                request.getDepartment(),
                request.getIsManager(),
                request.getGroup(),
                request.getMainPhoneNumber(),
                request.getAlternativePhoneNumber(),
                request.getTelegram(),
                request.getIfUnavailable()
        );
        return EmployeeResponse.of(employee);
    }

    @DeleteMapping(BaseRoutes.EMPLOYEE_BY_ID)
    public String deleteEmployee(@PathVariable Long id) throws NotFoundException {
        EmployeeService.deleteEmployee(employeeRepository, scheduleRepository, id);

        return HttpStatus.OK.name();
    }
}
