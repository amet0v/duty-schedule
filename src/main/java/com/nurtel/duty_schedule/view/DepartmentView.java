package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;

@Route(value = "/departments", layout = MainLayout.class)
public class DepartmentView extends VerticalLayout {

    public DepartmentView(DepartmentRepository departmentRepository) {

        Grid<DepartmentEntity> departmentEntityGrid = new Grid<>(DepartmentEntity.class);
        add(departmentEntityGrid);

        departmentEntityGrid.setColumns("name");
        departmentEntityGrid.getColumnByKey("name").setHeader("Название отдела");

        departmentEntityGrid.addColumn(department -> {
            return department.getEmployees()
                    .stream()
                    .map(EmployeeEntity::getFullName)
                    .collect(Collectors.joining(", "));
        }).setHeader("Сотрудники");

        departmentEntityGrid.setItems(departmentRepository.findAll());
    }
}
