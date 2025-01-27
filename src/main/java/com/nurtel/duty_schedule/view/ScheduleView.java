package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import com.nurtel.duty_schedule.schedule.repository.ScheduleRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route("schedule")
@PermitAll
public class ScheduleView extends VerticalLayout {

    @Autowired
    public ScheduleView(DepartmentRepository departmentRepository,
                        ScheduleRepository scheduleRepository) {

        var departments = departmentRepository.findAll(Sort.by(Sort.Order.asc("id")));

        for (DepartmentEntity department : departments){
            H3 departmentHeader = new H3(department.getName());

            Grid<EmployeeEntity> employeeEntityGrid = new Grid<>(EmployeeEntity.class);
            add(employeeEntityGrid);
            employeeEntityGrid.removeAllColumns();

            employeeEntityGrid.addColumn(EmployeeEntity::getFullName)
                    .setHeader("Сотрудник")
                    .setSortable(true)
                    .setFrozen(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getDepartment().getName())
                    .setHeader("Отдел")
                    .setSortable(true);

            employeeEntityGrid.addColumn(EmployeeEntity::getGroup)
                    .setHeader("Группа")
                    .setSortable(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getMainPhoneNumber() != null ? employee.getMainPhoneNumber() : "Не указан")
                    .setHeader("Основной телефон")
                    .setSortable(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getAlternativePhoneNumber() != null ? employee.getAlternativePhoneNumber() : "Не указан")
                    .setHeader("Доп. телефон")
                    .setSortable(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getTelegram() != null ? employee.getTelegram() : "Не указан")
                    .setHeader("Telegram")
                    .setSortable(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getIfUnavailable() != null ? employee.getIfUnavailable().getFullName() : "Не указан")
                    .setHeader("Если сотрудник недоступен")
                    .setSortable(true);

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(30);

            List<ScheduleEntity> events = scheduleRepository.findAllByDateRange(startDate, endDate);

            IntStream.range(0, 31).forEach(dayOffset -> {
                LocalDate currentDate = startDate.plusDays(dayOffset);
                String columnHeader = currentDate.format(DateTimeFormatter.ofPattern("dd.MM"));

                employeeEntityGrid.addColumn(employee -> {
                    Optional<ScheduleEntity> matchingEvent = events.stream()
                            .filter(event -> event.getEmployee().getId().equals(employee.getId()) &&
                                    !currentDate.isBefore(event.getStartDate()) &&
                                    !currentDate.isAfter(event.getEndDate()))
                            .findFirst();

                    if (matchingEvent.isPresent() && matchingEvent.get().getEvent() == EventTypes.Duty) return "\uD83D\uDEE0";
                    else if (matchingEvent.isPresent() && matchingEvent.get().getEvent() == EventTypes.Vacation) return "\uD83C\uDFD6";
                    else return "";
                }).setHeader(columnHeader);
            });

//            IntStream.range(0, 31).forEach(dayOffset -> {
//                LocalDate currentDate = LocalDate.now().plusDays(dayOffset);
//                String columnHeader = currentDate.format(DateTimeFormatter.ofPattern("dd.MM")); // Заголовок столбца — дата
//
//
//
//                employeeEntityGrid.addColumn(employee -> {
//                    Optional<ScheduleEntity> event = scheduleRepository.findAllEventsByEmployeeAndDate(
//                            employee.getId(),
//                            currentDate
//                    );
//                    if (event.isPresent() && event.get().getEvent() == EventTypes.Duty) return "\uD83D\uDEE0";
//                    else if (event.isPresent() && event.get().getEvent() == EventTypes.Vacation) return "\uD83C\uDFD6";
//                    else return "";
//                }).setHeader(columnHeader);
//            });

            employeeEntityGrid.setItems(department.getEmployees());

            Div departmentDiv = new Div();
            departmentDiv.add(departmentHeader, employeeEntityGrid);

            departmentDiv.getStyle().set("width", "100%");

            add(departmentDiv);
        }
    }
}
