package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import com.nurtel.duty_schedule.schedule.repository.ScheduleRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Route(value = "schedule", layout = MainLayout.class)
@PageTitle("График дежурств")
public class ScheduleView extends VerticalLayout {

    @Autowired
    public ScheduleView(DepartmentRepository departmentRepository,
                        ScheduleRepository scheduleRepository) {

        var departments = departmentRepository.findAll(Sort.by(Sort.Order.asc("id")));

        for (DepartmentEntity department : departments) {
            H3 departmentHeader = new H3(department.getName());

            Grid<EmployeeEntity> employeeEntityGrid = new Grid<>(EmployeeEntity.class);
            add(employeeEntityGrid);
            employeeEntityGrid.removeAllColumns();

            employeeEntityGrid.addColumn(EmployeeEntity::getFullName)
                    .setHeader("Сотрудник")
                    .setSortable(true)
                    .setFrozen(true)
                    .setAutoWidth(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getDepartment().getName())
                    .setHeader("Отдел")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeeEntityGrid.addColumn(EmployeeEntity::getGroup)
                    .setHeader("Группа")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getMainPhoneNumber() != null ? employee.getMainPhoneNumber() : "Не указан")
                    .setHeader("Основной телефон")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getAlternativePhoneNumber() != null ? employee.getAlternativePhoneNumber() : "Не указан")
                    .setHeader("Доп. телефон")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getTelegram() != null ? employee.getTelegram() : "Не указан")
                    .setHeader("Telegram")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeeEntityGrid.addColumn(employee ->
                            employee.getIfUnavailable() != null ? employee.getIfUnavailable().getFullName() : "Не указан")
                    .setHeader("Если сотрудник недоступен")
                    .setSortable(true)
                    .setAutoWidth(true);

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(30);

            List<ScheduleEntity> events = scheduleRepository.findAllByDateRange(startDate, endDate);

            IntStream.range(0, 31).forEach(dayOffset -> {
                LocalDate currentDate = startDate.plusDays(dayOffset);
                String columnHeader = currentDate.format(DateTimeFormatter.ofPattern("dd.MM"));

                boolean isWeekend = currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY;
                if (isWeekend) columnHeader += "\uD83D\uDFE5";
                else columnHeader += "\uD83D\uDFE6";

                employeeEntityGrid.addColumn(new ComponentRenderer<>(employee -> {
                    Optional<ScheduleEntity> matchingEvent = events.stream()
                            .filter(event -> event.getEmployee().getId().equals(employee.getId()) &&
                                    !currentDate.isBefore(event.getStartDate()) &&
                                    !currentDate.isAfter(event.getEndDate()))
                            .findFirst();

                    String dutyIcon = "\uD83D\uDEE0";
                    String vacationIcon = "\uD83C\uDFD6";

                    if (matchingEvent.isPresent() && matchingEvent.get().getEvent() == EventTypes.Duty) {
                        if (MainLayout.isAuthenticated()) {
                            Button button = new Button(dutyIcon);
                            button.setWidth("50px");
                            button.getElement().getStyle().set("min-width", "0px");
                            button.setHeight("30px");
                            button.getElement().getStyle().set("font-size", "20px");
                            button.getElement().getStyle().set("padding", "0");
                            button.getElement().getStyle().set("margin", "0");

                            button.addClickListener(e -> {
                                Optional<ScheduleEntity> duty = scheduleRepository.findAllEventsByEmployeeAndDate(
                                        employee.getId(), currentDate
                                );
                                duty.ifPresent(schedule -> scheduleRepository.deleteById(schedule.getId()));

                                refresh(events, scheduleRepository, startDate, endDate, employeeEntityGrid);
                            });
                            return button;
                        } else {
                            Span dutySpan = new Span(dutyIcon);
                            dutySpan.getStyle()
                                    .set("text-align", "center")
                                    .set("display", "flex")
                                    .set("justify-content", "center")
                                    .set("align-items", "center")
                                    .set("font-size", "20px");
                            return dutySpan;
                        }
                    } else if (matchingEvent.isPresent() && matchingEvent.get().getEvent() == EventTypes.Vacation) {
                        if (MainLayout.isAuthenticated()) {
                            Button button = new Button(vacationIcon);
                            button.setWidth("50px");
                            button.getElement().getStyle().set("min-width", "0px");
                            button.setHeight("30px");
                            button.getElement().getStyle().set("font-size", "20px");
                            button.getElement().getStyle().set("padding", "0");
                            button.getElement().getStyle().set("margin", "0");

                            button.addClickListener(e -> {
                                Optional<ScheduleEntity> duty = scheduleRepository.findAllEventsByEmployeeAndDate(
                                        employee.getId(), currentDate
                                );
                                duty.ifPresent(schedule -> scheduleRepository.deleteById(schedule.getId()));
                                refresh(events, scheduleRepository, startDate, endDate, employeeEntityGrid);
                            });
                            return button;
                        } else {
                            Span vacationSpan = new Span(vacationIcon);
                            vacationSpan.getStyle()
                                    .set("text-align", "center")
                                    .set("display", "flex")
                                    .set("justify-content", "center")
                                    .set("align-items", "center")
                                    .set("font-size", "20px");
                            return vacationSpan;
                        }
                    } else {
                        ComboBox<String> comboBox = new ComboBox<>();
                        comboBox.setItems(dutyIcon, vacationIcon);
                        comboBox.setPlaceholder("+");
                        comboBox.getElement().getStyle().set("font-size", "14px");
                        comboBox.getElement().getStyle().set("padding", "0");
                        comboBox.getElement().getStyle().set("margin", "0");
                        comboBox.getElement().getStyle().set("text-align", "center");
                        comboBox.setWidth("50px");
                        comboBox.setVisible(MainLayout.isAuthenticated());
                        comboBox.addValueChangeListener(e -> {
                            ScheduleEntity scheduleEntity;
                            if (e.getValue().equals(dutyIcon)) {
                                scheduleEntity = ScheduleEntity.builder()
                                        .employee(employee)
                                        .startDate(currentDate)
                                        .endDate(currentDate)
                                        .event(EventTypes.Duty)
                                        .build();
                                Optional<ScheduleEntity> checkDuty = scheduleRepository.findDutyByDepartmentAndDate(
                                        employee.getDepartment().getId(),
                                        currentDate,
                                        EventTypes.Duty
                                );
                                if (checkDuty.isEmpty()) scheduleRepository.save(scheduleEntity);
                                else {
                                    Notification.show("На эту дату уже назначен дежурный", 5000,
                                            Notification.Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
                                    events.clear();
                                    events.addAll(scheduleRepository.findAllByDateRange(startDate, endDate));
                                    employeeEntityGrid.getDataProvider().refreshAll();
                                }
                            } else {
                                scheduleEntity = ScheduleEntity.builder()
                                        .employee(employee)
                                        .startDate(currentDate)
                                        .endDate(currentDate)
                                        .event(EventTypes.Vacation)
                                        .build();
                                scheduleRepository.save(scheduleEntity);
                            }
                            refresh(events, scheduleRepository, startDate, endDate, employeeEntityGrid);
                        });
                        return comboBox;
                    }
                })).setHeader(columnHeader).setAutoWidth(true);
            });

            List<EmployeeEntity> sortedEmployees = department.getEmployees().stream()
                    .sorted((e1, e2) -> Boolean.compare(e2.getIsManager(), e1.getIsManager()))
                    .toList();

            employeeEntityGrid.setItems(sortedEmployees);

            employeeEntityGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
            employeeEntityGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            ;

            Div departmentDiv = new Div();
            departmentDiv.add(departmentHeader, employeeEntityGrid);

            departmentDiv.getStyle().set("width", "100%");

            add(departmentDiv);
        }
    }

    private void refresh(
            List<ScheduleEntity> events,
            ScheduleRepository scheduleRepository,
            LocalDate startDate,
            LocalDate endDate,
            Grid<EmployeeEntity> grid
    ) {
        events.clear();
        events.addAll(scheduleRepository.findAllByDateRange(startDate, endDate));
        grid.getDataProvider().refreshAll();
    }
}
