package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

@Route(value = "/employees", layout = MainLayout.class)
@PageTitle("Сотрудники")
public class EmployeeView extends VerticalLayout {

    @Autowired
    public EmployeeView(EmployeeRepository employeeRepository) {

        Grid<EmployeeEntity> employeeEntityGrid = new Grid<>(EmployeeEntity.class);
        add(employeeEntityGrid);

        employeeEntityGrid.removeAllColumns();

        employeeEntityGrid.addColumn(EmployeeEntity::getFullName)
                .setHeader("Сотрудник")
                .setSortable(true);

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

        employeeEntityGrid.addColumn(employee ->
                        employee.getIsManager() ? "Да" : "Нет")
                .setHeader("Руководитель?")
                .setSortable(true);

        employeeEntityGrid.setItems(employeeRepository.findAll(
                Sort.by(Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
        ));
    }
}
