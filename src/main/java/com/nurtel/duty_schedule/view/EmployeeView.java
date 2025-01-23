package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/employees")
public class EmployeeView extends VerticalLayout {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeView(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;

        Grid<EmployeeEntity> employeeEntityGrid = new Grid<>(EmployeeEntity.class);
        add(employeeEntityGrid);

        // Указываем порядок отображения колонок
        employeeEntityGrid.setColumns(
                "fullName",
                "department.name",
                "isManager",
                "group",
                "mainPhoneNumber",
                "alternativePhoneNumber",
                "telegram",
                "ifUnavailable.fullName"
        );

        employeeEntityGrid.getColumnByKey("fullName").setHeader("Сотрудник");
        employeeEntityGrid.getColumnByKey("department.name").setHeader("Отдел");
        employeeEntityGrid.getColumnByKey("isManager").setHeader("Менеджер");
        employeeEntityGrid.getColumnByKey("group").setHeader("Группа");
        employeeEntityGrid.getColumnByKey("mainPhoneNumber").setHeader("Основной телефон");
        employeeEntityGrid.getColumnByKey("alternativePhoneNumber").setHeader("Доп. телефон");
        employeeEntityGrid.getColumnByKey("telegram").setHeader("Telegram");
        employeeEntityGrid.getColumnByKey("ifUnavailable.fullName").setHeader("Если сотрудник недоступен");

        employeeEntityGrid.setItems(employeeRepository.findAll());
    }
}
