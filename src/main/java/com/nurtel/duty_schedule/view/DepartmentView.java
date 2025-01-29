package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;

@Route(value = "/departments", layout = MainLayout.class)
@PageTitle("Отделы")
public class DepartmentView extends VerticalLayout {

    public DepartmentView(DepartmentRepository departmentRepository) {
        Grid<DepartmentEntity> departmentEntityGrid = new Grid<>(DepartmentEntity.class);

        Button addButton = createDepartmentButton(departmentRepository, departmentEntityGrid);
        Button deleteButton = deleteDepartmentButton(departmentRepository, departmentEntityGrid);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(addButton, deleteButton);
        add(horizontalLayout);

        add(departmentEntityGrid);

        departmentEntityGrid.setColumns("name");
        departmentEntityGrid.getColumnByKey("name").setHeader("Название отдела");

        departmentEntityGrid.addColumn(department -> department.getEmployees()
                .stream()
                .map(EmployeeEntity::getFullName)
                .collect(Collectors.joining(", "))).setHeader("Сотрудники");

        departmentEntityGrid.setItems(departmentRepository.findAll());
    }

    private Button createDepartmentButton(DepartmentRepository repository, Grid<DepartmentEntity> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Новый отдел");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        TextField departmentNameField = new TextField("Название отдела");
        dialogLayout.add(departmentNameField);

        Button saveButton = new Button("Сохранить", e -> {
            String departmentName = departmentNameField.getValue();
            if (departmentName != null && !departmentName.trim().isEmpty()) {
                DepartmentEntity department = DepartmentEntity.builder()
                        .name(departmentNameField.getValue())
                        .build();

                repository.save(department);
                departmentNameField.clear();
                //dialog.close();
                grid.setItems(repository.findAll());
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(saveButton, cancelButton);

        Button addDepartmentbutton = new Button("Добавить", e -> {
            departmentNameField.clear();
            dialog.open();
        });
        addDepartmentbutton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(dialog);

        return addDepartmentbutton;
    }

    private Button deleteDepartmentButton(DepartmentRepository repository, Grid<DepartmentEntity> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Удалить отдел");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        ComboBox<DepartmentEntity> departmentComboBox = new ComboBox<>("Название отдела");
        departmentComboBox.setItems(repository.findAll());
        departmentComboBox.setItemLabelGenerator(DepartmentEntity::getName);
        dialogLayout.add(departmentComboBox);

        Button deleteButton = new Button("Удалить", e -> {
            DepartmentEntity selectedDepartment = departmentComboBox.getValue();
            if (selectedDepartment != null) {
                repository.delete(selectedDepartment);
                //dialog.close();
                grid.setItems(repository.findAll());
                departmentComboBox.setItems(repository.findAll());
            } else {
                Notification.show("Выберите отдел для удаления", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(deleteButton, cancelButton);

        Button deleteDepartmentButton = new Button("Удалить", e -> {
            departmentComboBox.clear();
            departmentComboBox.setItems(repository.findAll());
            dialog.open();
        });
        deleteDepartmentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        add(dialog);

        return deleteDepartmentButton;
    }
}
