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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeService {
    private static DepartmentEntity departmentCheck(DepartmentRepository departmentRepository, DepartmentEntity department) throws NotFoundException {
        if (department != null) {
            if (department.getId() == null && department.getName() == null)
                throw new BadRequestException("Необходимо передать данные по отделу (id или название)");
            else {
                Optional<DepartmentEntity> departmentEntityOptional = Optional.empty();
                if (department.getName() == null) {
                    departmentEntityOptional = departmentRepository.findById(department.getId());
                    if (departmentEntityOptional.isEmpty())
                        throw new NotFoundException("Отдел с указанным id не найден");
                    return departmentEntityOptional.get();
                }
                if (department.getId() == null) {
                    departmentEntityOptional = departmentRepository.findByName(department.getName());
                    if (departmentEntityOptional.isEmpty()) {
                        department = DepartmentService.createDepartment(departmentRepository, department.getName());
                        department = departmentRepository.save(department);
                        return department;
                    } else return departmentEntityOptional.get();
                }
            }
        } else throw new BadRequestException("Необходимо указать отдел сотрудника");
        return department;
    }

    private static EmployeeEntity ifUnavailableCheck(
            EmployeeRepository employeeRepository,
            EmployeeEntity ifUnavailable,
            DepartmentEntity department
    ) throws NotFoundException {
        if (ifUnavailable != null) {
            if (ifUnavailable.getId() == null) throw new NotFoundException("Сотрудник с указанным id не найден");
            Optional<EmployeeEntity> ifUnavailableCheck = employeeRepository.findById(ifUnavailable.getId());
            if (ifUnavailableCheck.isPresent()) {
                EmployeeEntity foundEmployee = ifUnavailableCheck.get();
                if (Objects.equals(foundEmployee.getDepartment().getId(), department.getId())) {
                    return foundEmployee;
                } else {
                    throw new BadRequestException("Сотрудник с указанным id находится в другом отделе");
                }
            } else {
                throw new NotFoundException("Сотрудник с указанным id не найден");
            }
        }
        return null;
    }

    @Transactional
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

        department = departmentCheck(departmentRepository, department);

        ifUnavailable = ifUnavailableCheck(employeeRepository, ifUnavailable, department);

        isManager = isManager != null ? isManager : false;

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
        if (manager.isPresent()) {
            if (isManager.equals(true)) throw new BadRequestException("У этого отдела уже есть руководитель");
            else {
                employee.setManager(manager.get());
                employeeRepository.save(employee);
            }
        } else {
            if (isManager.equals(true)) {
                List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(department.getId());
                for (EmployeeEntity employeeEntity : employees) {
                    employeeEntity.setManager(employee);
                }
                employee = employeeRepository.save(employee);
                employeeRepository.saveAll(employees);
            } else {
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

        if (isManager == null) isManager = false;

        if (department != null) {
            department = departmentRepository.findById(
                    department.getId()).orElseThrow(() -> new NotFoundException("Отдел не найден")
            );
        } else department = employee.getDepartment();

        if (!Objects.equals(department.getId(), employee.getDepartment().getId())) {

            if (employee.getIsManager().equals(true)) {
                List<EmployeeEntity> employeeEntities = employeeRepository.findAllByDepartment(employee.getDepartment().getId());
                for (EmployeeEntity employeeEntity : employeeEntities) {
                    employeeEntity.setManager(null);
                }
                employeeRepository.saveAll(employeeEntities);
            }

            //employee.setManager(null);
            employee.setIfUnavailable(null);
            Optional<EmployeeEntity> manager = employeeRepository.findManagerByDepartmentId(department.getId());
            if (manager.isPresent()) {
                if (isManager.equals(false)) employee.setManager(manager.get());
                else throw new BadRequestException("У этого отдела уже есть руководитель");
            } else {
                if (isManager.equals(true)) {
                    List<EmployeeEntity> employeeEntities = employeeRepository.findAllByDepartment(department.getId());
                    for (EmployeeEntity employeeEntity : employeeEntities) {
                        employeeEntity.setManager(employee);
                    }
                    employeeRepository.saveAll(employeeEntities);
                }
            }
        } else {
            Optional<EmployeeEntity> manager = employeeRepository.findManagerByDepartmentId(department.getId());
            if (manager.isPresent()) {
                if (Objects.equals(manager.get().getId(), employee.getId())) {
                    if (isManager.equals(false)) {
                        List<EmployeeEntity> employeeEntities = employeeRepository.findAllByDepartment(department.getId());
                        for (EmployeeEntity employeeEntity : employeeEntities) {
                            employeeEntity.setManager(null);
                        }
                        employeeRepository.saveAll(employeeEntities);
                    }
                } else {
                    if (isManager.equals(true)) throw new BadRequestException("У этого отдела уже есть руководитель");
                }
            } else {
                if (isManager.equals(true)) {
                    List<EmployeeEntity> employeeEntities = employeeRepository.findAllByDepartment(department.getId());
                    for (EmployeeEntity employeeEntity : employeeEntities) {
                        if (!Objects.equals(employeeEntity.getId(), employee.getId()))
                            employeeEntity.setManager(employee);
                    }
                    employeeRepository.saveAll(employeeEntities);
                }
            }
        }

        ifUnavailable = ifUnavailableCheck(employeeRepository, ifUnavailable, department);

        if (fullName != null) employee.setFullName(fullName);
        employee.setDepartment(department);
        employee.setIsManager(isManager);
        if (group != null) employee.setGroup(group);
        if (mainPhoneNumber != null) employee.setMainPhoneNumber(mainPhoneNumber);
        if (altPhoneNumber != null) employee.setAlternativePhoneNumber(altPhoneNumber);
        if (telegram != null) employee.setTelegram(telegram);
        if (ifUnavailable != null) employee.setIfUnavailable(ifUnavailable);

        employeeRepository.save(employee);

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
            if (employee.getIsManager().equals(true)) entity.setManager(null);
            if (entity.getIfUnavailable() != null && Objects.equals(entity.getIfUnavailable().getId(), employee.getId()))
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
