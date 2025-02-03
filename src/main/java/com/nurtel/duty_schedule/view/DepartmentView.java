package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.department.service.DepartmentService;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.vaadin.flow.component.UI;
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
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "/departments", layout = MainLayout.class)
@PageTitle("Отделы")
public class DepartmentView extends VerticalLayout {
    public static Button addButton = new Button();
    public static Button deleteButton = new Button();
    public static Button editButton = new Button();

    private void updateButtonsVisibility() {
        boolean authenticated = MainLayout.isAuthenticated();
        UI.getCurrent().access(() -> {
            addButton.setVisible(authenticated);
            deleteButton.setVisible(authenticated);
            editButton.setVisible(authenticated);
        });
    }

    public DepartmentView(DepartmentRepository departmentRepository) {
        Grid<DepartmentEntity> departmentEntityGrid = new Grid<>(DepartmentEntity.class);

        addButton = createDepartmentButton(departmentRepository, departmentEntityGrid);
        deleteButton = deleteDepartmentButton(departmentRepository, departmentEntityGrid);
        editButton = editDepartmentButton(departmentRepository, departmentEntityGrid);

        addButton.setVisible(MainLayout.isAuthenticated());
        deleteButton.setVisible(MainLayout.isAuthenticated());
        editButton.setVisible(MainLayout.isAuthenticated());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(addButton, editButton, deleteButton);
        add(horizontalLayout);

        add(departmentEntityGrid);

        departmentEntityGrid.setColumns("name");
        departmentEntityGrid.getColumnByKey("name").setHeader("Название отдела");

        departmentEntityGrid.addColumn(department -> department.getEmployees()
                .stream()
                .map(EmployeeEntity::getFullName)
                .collect(Collectors.joining(", "))).setHeader("Сотрудники");

        departmentEntityGrid.setItems(setSortedItems(departmentRepository));

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
                try {
                    DepartmentService.createDepartment(repository, departmentName);
                } catch (BadRequestException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                departmentNameField.clear();
                //dialog.close();
                grid.setItems(setSortedItems(repository));
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

    private Button editDepartmentButton(DepartmentRepository repository, Grid<DepartmentEntity> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Редактировать отдел");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        ComboBox<DepartmentEntity> departmentComboBox = new ComboBox<>("Название отдела");
        departmentComboBox.setItems(setSortedItems(repository));
        departmentComboBox.setItemLabelGenerator(DepartmentEntity::getName);
        TextField departmentNameField = new TextField("Название отдела");
        dialogLayout.add(departmentComboBox, departmentNameField);

        departmentComboBox.addValueChangeListener(event -> {
            DepartmentEntity selectedDepartment = event.getValue();
            if (selectedDepartment != null) {
                departmentNameField.setValue(selectedDepartment.getName());
            } else {
                departmentNameField.clear();
            }
        });

        Button editButton = new Button("Сохранить", e -> {
            DepartmentEntity selectedDepartment = departmentComboBox.getValue();
            if (selectedDepartment != null && !departmentNameField.isEmpty()) {
                try {
                    DepartmentService.editDepartment(repository, selectedDepartment.getId(), departmentNameField.getValue());
                } catch (NotFoundException | BadRequestException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                grid.setItems(setSortedItems(repository));
                departmentComboBox.setItems(setSortedItems(repository));
            } else {
                Notification.show("Выберите отдел для редактирования", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(editButton, cancelButton);

        Button editDepartmentButton = new Button("Редактировать", e -> {
            departmentComboBox.clear();
            departmentComboBox.setItems(setSortedItems(repository));
            dialog.open();
        });
        editDepartmentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(dialog);

        return editDepartmentButton;
    }

    private Button deleteDepartmentButton(DepartmentRepository repository, Grid<DepartmentEntity> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Удалить отдел");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        ComboBox<DepartmentEntity> departmentComboBox = new ComboBox<>("Название отдела");
        departmentComboBox.setItems(setSortedItems(repository));
        departmentComboBox.setItemLabelGenerator(DepartmentEntity::getName);
        dialogLayout.add(departmentComboBox);

        Button deleteButton = new Button("Удалить", e -> {
            DepartmentEntity selectedDepartment = departmentComboBox.getValue();
            if (selectedDepartment != null) {
                try {
                    DepartmentService.deleteDepartment(repository, selectedDepartment.getId());
                } catch (NotFoundException | BadRequestException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                //dialog.close();
                grid.setItems(setSortedItems(repository));
                departmentComboBox.setItems(setSortedItems(repository));
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
            departmentComboBox.setItems(setSortedItems(repository));
            dialog.open();
        });
        deleteDepartmentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        add(dialog);

        return deleteDepartmentButton;
    }

    private List<DepartmentEntity> setSortedItems(DepartmentRepository repository){
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
