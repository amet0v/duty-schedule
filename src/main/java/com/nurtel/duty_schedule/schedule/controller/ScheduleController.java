package com.nurtel.duty_schedule.schedule.controller;

import com.nurtel.duty_schedule.employee.dto.response.EmployeeResponse;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.routes.BaseRoutes;
import com.nurtel.duty_schedule.schedule.dto.request.ScheduleRequest;
import com.nurtel.duty_schedule.schedule.dto.response.ScheduleResponse;
import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import com.nurtel.duty_schedule.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping(BaseRoutes.SCHEDULE_GET_DUTY_BY_DEPARTMENT)
    public EmployeeResponse getTodayDutyForDepartment(@PathVariable Long departmentId) throws NotFoundException {
        LocalDate currentDate = LocalDate.now();
        EmployeeEntity employee;

        Optional<ScheduleEntity> duty = scheduleRepository.findDutyByDepartmentAndDate(departmentId, currentDate, EventTypes.Duty);

        if (duty.isEmpty()) {
            List<EmployeeEntity> employees = employeeRepository.findDutyByDepartmentIdOrderByLastCallDateAsc(departmentId);
            employees.sort(Comparator.comparing(EmployeeEntity::getLastCallDate, Comparator.nullsFirst(Comparator.naturalOrder())));
            employee = employees.getFirst();
        }else {
            employee = duty.get().getEmployee();
        }
        employee.setLastCallDate(new Date());
        employee = employeeRepository.save(employee);
        return EmployeeResponse.of(employee);
    }

    @GetMapping(BaseRoutes.SCHEDULE)
    public List<ScheduleResponse> getEmployeeEvents(
            @PathVariable Long employeeId,
            @RequestParam(required = false) EventTypes event
    ) {
        List<ScheduleEntity> events;

        if (event == null) {
            events = scheduleRepository.findAllEventsByEmployee(employeeId);
        } else {
            events = scheduleRepository.findEventsByEmployee(employeeId, event);
        }

        return events.stream().map(ScheduleResponse::of).collect(Collectors.toList());
    }

    @PostMapping(BaseRoutes.SCHEDULE)
    public ScheduleResponse createEvent(@RequestBody ScheduleRequest request) throws BadRequestException, NotFoundException {
        request.validate();

        EmployeeEntity employee = employeeRepository.findById(request.getEmployee().getId()).orElseThrow();

        Optional<ScheduleEntity> duty = scheduleRepository.findDutyByDepartmentAndDate(
                employee.getDepartment().getId(),
                request.getStartDate(),
                EventTypes.Duty
        );
        if (duty.isPresent() && request.getEvent() == EventTypes.Duty) throw new BadRequestException("На эту дату уже назначен дежурный");

        duty = scheduleRepository.findAllEventsByEmployeeAndDate(
                employee.getId(), request.getStartDate()
        );
        if (duty.isPresent()) throw new BadRequestException("Для этого сотрудника уже назначено событие на эту дату");

        ScheduleEntity schedule = ScheduleEntity.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .employee(employee)
                .event(request.getEvent())
                .build();

        schedule = scheduleRepository.save(schedule);
        return ScheduleResponse.of(schedule);
    }

    @DeleteMapping(BaseRoutes.SCHEDULE_BY_ID)
    public String deleteEvent(@PathVariable Long id){
        scheduleRepository.deleteById(id);
        return HttpStatus.OK.name();
    }
}
