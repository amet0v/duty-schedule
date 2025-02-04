package com.nurtel.duty_schedule.employee.service;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.department.service.DepartmentService;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import com.nurtel.duty_schedule.schedule.repository.ScheduleRepository;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EmployeeService {
    public static EmployeeEntity createEmployee(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            String fullName,
            DepartmentEntity department,
            Boolean isManager,
            String group,
            String mainPhoneNumber,
            String altPhoneNumber,
            String telegram,
            EmployeeEntity ifUnavailable
    ) throws NotFoundException, BadRequestException {
        if (department != null) {
            if (department.getId() == null && department.getName() == null)
                throw new BadRequestException("Необходимо передать данные по отделу (id или название)");
            if (department.getName() != null && department.getId() == null) {
                department = DepartmentService.createDepartment(departmentRepository, department.getName());
            } else {
                Optional<DepartmentEntity> departmentEntityOptional = departmentRepository.findById(department.getId());
                if (departmentEntityOptional.isEmpty()) throw new NotFoundException("Отдел с указанным id не найден");
                else department = departmentEntityOptional.get();
            }
        } else throw new BadRequestException("Необходимо указать отдел сотрудника");

        if (ifUnavailable != null) {
            if (ifUnavailable.getId() == null) throw new NotFoundException("Сотрудник с указанным id не найден");
            Optional<EmployeeEntity> ifUnavailableCheck = employeeRepository.findById(ifUnavailable.getId());
            if (ifUnavailableCheck.isPresent() && ifUnavailableCheck.get().getDepartment() == department)
                ifUnavailable = ifUnavailableCheck.get();
            else {
                if (ifUnavailableCheck.isEmpty()) throw new NotFoundException("Сотрудник с указанным id не найден");
                else throw new BadRequestException("Сотрудник с указанным id находится в другом отделе");
            }
        }

        EmployeeEntity employee = EmployeeEntity.builder()
                .fullName(fullName)
                .department(department)
                .isManager(isManager)
                .group(group)
                .mainPhoneNumber(mainPhoneNumber)
                .alternativePhoneNumber(altPhoneNumber)
                .telegram(telegram)
                .ifUnavailable(ifUnavailable)
                .build();

        Optional<EmployeeEntity> manager = employeeRepository.findManagerByDepartmentId(department.getId());
        if (isManager.equals(true) && manager.isPresent()) {
            throw new BadRequestException("У этого отдела уже есть руководитель");
        } else {
            if (isManager.equals(true) && manager.isEmpty()) {
                employee = employeeRepository.save(employee);

                List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(department.getId());
                for (EmployeeEntity employeeEntity : employees) {
                    employeeEntity.setManager(employee);
                }
                employeeRepository.saveAll(employees);

            } else if (isManager.equals(false) && manager.isPresent()) {
                employee.setManager(manager.get());
                employeeRepository.save(employee);
            } else if (isManager.equals(false) && manager.isEmpty()) {
                employeeRepository.save(employee);
            }
        }
        return employee;
    }

    @Transactional
    public static EmployeeEntity editEmployee(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            Long id,
            String fullName,
            DepartmentEntity department,
            Boolean isManager,
            String group,
            String mainPhoneNumber,
            String altPhoneNumber,
            String telegram,
            EmployeeEntity ifUnavailable
    ) throws NotFoundException {
        EmployeeEntity employee;
        Optional<EmployeeEntity> employeeCheck = employeeRepository.findById(id);
        if (employeeCheck.isEmpty()) throw new NotFoundException("Сотрудник с указанным id не найден");
        else employee = employeeCheck.get();

        if (department != null) {
            if (department.getId() == null && department.getName() == null)
                throw new BadRequestException("Необходимо передать данные по отделу (id или название)");
            if (department.getName() != null && department.getId() == null) {
                department = DepartmentService.createDepartment(departmentRepository, department.getName());
            } else {
                Optional<DepartmentEntity> departmentEntityOptional = departmentRepository.findById(department.getId());
                if (departmentEntityOptional.isEmpty()) throw new NotFoundException("Отдел с указанным id не найден");
                else department = departmentEntityOptional.get();
                if (employee.getDepartment() != department) {
                    employee.setIfUnavailable(null);
                    employee.setManager(null);

                    List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(employee.getDepartment().getId());
                    for (EmployeeEntity entity : employees) {
                        if (employee.getIsManager()) entity.setManager(null);
                        if (entity.getIfUnavailable() != null && entity.getIfUnavailable().getId() == employee.getId())
                            entity.setIfUnavailable(null);
                        employeeRepository.saveAll(employees);
                    }

                    employee.setDepartment(department);
                }
            }
        } else throw new BadRequestException("Необходимо указать отдел сотрудника");

        if (ifUnavailable != null) {
            if (ifUnavailable.getId() == null) throw new NotFoundException("Сотрудник с указанным id не найден");
            Optional<EmployeeEntity> ifUnavailableCheck = employeeRepository.findById(ifUnavailable.getId());
            if (ifUnavailableCheck.isPresent() && ifUnavailableCheck.get().getDepartment() == department)
                ifUnavailable = ifUnavailableCheck.get();
            else {
                if (ifUnavailableCheck.isEmpty()) throw new NotFoundException("Сотрудник с указанным id не найден");
                else throw new BadRequestException("Сотрудник с указанным id находится в другом отделе");
            }
        }

        if (fullName != null) employee.setFullName(fullName);
        if (isManager == null || !isManager) {
            isManager = false;
            employee.setIsManager(isManager);
        }
        if (group != null) employee.setGroup(group);
        if (mainPhoneNumber != null) employee.setMainPhoneNumber(mainPhoneNumber);
        if (altPhoneNumber != null) employee.setAlternativePhoneNumber(altPhoneNumber);
        if (telegram != null) employee.setTelegram(telegram);
        if (ifUnavailable != null) employee.setIfUnavailable(ifUnavailable);

        Optional<EmployeeEntity> manager = employeeRepository.findManagerByDepartmentId(department.getId());
        if (isManager.equals(true) && manager.isPresent()) {
            Notification.show("У этого отдела уже есть руководитель", 5000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (isManager.equals(true) && manager.isEmpty()) {
                employee.setIsManager(true);
                employeeRepository.save(employee);

                List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(department.getId());
                for (EmployeeEntity employeeEntity : employees) {
                    employeeEntity.setManager(employee);
                }
                employeeRepository.saveAll(employees);

            } else if (isManager.equals(false) && manager.isPresent()) {
                employee.setManager(manager.get());
                employeeRepository.save(employee);
            } else if (isManager.equals(false) && manager.isEmpty()) {
                employee.setManager(null);
                employeeRepository.save(employee);
            }
        }
        return employee;
    }

    @Transactional
    public static void deleteEmployee(
            EmployeeRepository employeeRepository,
            ScheduleRepository scheduleRepository,
            Long id
    ) throws NotFoundException {
        EmployeeEntity employee;
        Optional<EmployeeEntity> employeeCheck = employeeRepository.findById(id);
        if (employeeCheck.isEmpty()) throw new NotFoundException("Сотрудник с указанным id не найден");
        else employee = employeeCheck.get();

        List<ScheduleEntity> scheduleEntityList = scheduleRepository.findAllEventsByEmployee(id);
        scheduleRepository.deleteAll(scheduleEntityList);

        List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(employee.getDepartment().getId());
        for (EmployeeEntity entity : employees) {
            if (employee.getIsManager()) entity.setManager(null);
            if (entity.getIfUnavailable() != null && entity.getIfUnavailable().getId() == employee.getId())
                entity.setIfUnavailable(null);
        }
        employeeRepository.saveAll(employees);

        employee.setIfUnavailable(null);
        employee.setManager(null);
        employee.setDepartment(null);

        employeeRepository.save(employee);
        employeeRepository.delete(employee);
    }
}
