package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.employee.service.EmployeeService;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.schedule.repository.ScheduleRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Route(value = "/employees", layout = MainLayout.class)
@PageTitle("Сотрудники")
public class EmployeeView extends VerticalLayout {
    public static Button addButton = new Button();
    public static Button deleteButton = new Button();
    public static Button editButton = new Button();


    @Autowired
    public EmployeeView(
            EmployeeRepository employeeRepository,
            ScheduleRepository scheduleRepository,
            DepartmentRepository departmentRepository
    ) {
        Grid<EmployeeEntity> employeeEntityGrid = new Grid<>(EmployeeEntity.class);

        deleteButton = deleteEmployeeButton(employeeRepository, employeeEntityGrid, scheduleRepository);
        addButton = addEmployeeButton(employeeRepository, employeeEntityGrid, departmentRepository);
        editButton = editEmployeeButton(employeeRepository, employeeEntityGrid, departmentRepository);

        addButton.setVisible(MainLayout.isAuthenticated());
        deleteButton.setVisible(MainLayout.isAuthenticated());
        editButton.setVisible(MainLayout.isAuthenticated());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(addButton, editButton, deleteButton);
        add(horizontalLayout);

        add(employeeEntityGrid);

        employeeEntityGrid.removeAllColumns();

        employeeEntityGrid.addColumn(EmployeeEntity::getId)
                .setHeader("ID")
                .setSortable(true);

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

        employeeEntityGrid.addColumn(employee ->
                        employee.getManager() != null ? employee.getManager().getFullName() : "Не указан")
                .setHeader("Руководитель")
                .setSortable(true);

        employeeEntityGrid.setItems(employeeRepository.findAll(
                Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
        ));
    }

    private Button addEmployeeButton(
            EmployeeRepository employeeRepository,
            Grid<EmployeeEntity> grid,
            DepartmentRepository departmentRepository
    ) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Добавить сотрудника");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        TextField fullNameField = new TextField("Фамилия Имя");
        ComboBox<DepartmentEntity> departmentComboBox = new ComboBox<>("Отдел");
        departmentComboBox.setItems(departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        departmentComboBox.setItemLabelGenerator(DepartmentEntity::getName);
        Checkbox isManagerCheckbox = new Checkbox("Руководитель?");
        TextField groupField = new TextField("Группа");
        TextField mainPhoneNumberField = new TextField("Основной тел. номер");
        TextField altPhoneNumberField = new TextField("Альтернативный тел. номер");
        TextField telegramField = new TextField("Телеграм");
        ComboBox<EmployeeEntity> ifUnavailableComboBox = new ComboBox<>("Если недоступен");

        dialogLayout.add(
                fullNameField,
                departmentComboBox,
                isManagerCheckbox,
                groupField,
                mainPhoneNumberField,
                altPhoneNumberField,
                telegramField,
                ifUnavailableComboBox
        );

        departmentComboBox.addValueChangeListener(event -> {
            DepartmentEntity selectedDepartment = event.getValue();
            if (selectedDepartment != null) {
                ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedDepartment.getId()));
            } else {
                ifUnavailableComboBox.clear();
                ifUnavailableComboBox.setItems();
            }
        });

        Button addButton = new Button("Добавить", e -> {
            DepartmentEntity selectedDepartment = departmentComboBox.getValue();
            if (selectedDepartment != null && !fullNameField.isEmpty()) {
                try {
                    EmployeeService.createEmployee(
                            departmentRepository,
                            employeeRepository,
                            fullNameField.getValue(),
                            selectedDepartment,
                            isManagerCheckbox.getValue(),
                            groupField.getValue(),
                            mainPhoneNumberField.getValue(),
                            altPhoneNumberField.getValue(),
                            telegramField.getValue(),
                            ifUnavailableComboBox.getValue()
                    );
                } catch (NotFoundException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                grid.setItems(employeeRepository.findAll(
                        Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
                ));
                ifUnavailableComboBox.clear();
                ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedDepartment.getId()));

                //dialog.close();
            } else {
                Notification.show("Заполните все обязательные поля", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(addButton, cancelButton);

        Button addEmployeeButton = new Button("Добавить", e -> {
            departmentComboBox.clear();
            ifUnavailableComboBox.clear();
            fullNameField.clear();
            groupField.clear();
            mainPhoneNumberField.clear();
            altPhoneNumberField.clear();
            telegramField.clear();
            isManagerCheckbox.clear();
            dialog.open();
        });
        addEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(dialog);

        return addEmployeeButton;
    }

    private Button editEmployeeButton(
            EmployeeRepository employeeRepository,
            Grid<EmployeeEntity> grid,
            DepartmentRepository departmentRepository
    ) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Редактировать сотрудника");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        ComboBox<EmployeeEntity> employeeComboBox = new ComboBox<>("Сотрудник");
        employeeComboBox.setItems(setSortedItems(employeeRepository));
        employeeComboBox.setItemLabelGenerator(EmployeeEntity::getFullName);

        TextField fullNameField = new TextField("Фамилия Имя");
        ComboBox<DepartmentEntity> departmentComboBox = new ComboBox<>("Отдел");
        departmentComboBox.setItems(departmentRepository.findAll());
        departmentComboBox.setItemLabelGenerator(DepartmentEntity::getName);
        Checkbox isManagerCheckbox = new Checkbox("Руководитель?");
        TextField groupField = new TextField("Группа");
        TextField mainPhoneNumberField = new TextField("Основной тел. номер");
        TextField altPhoneNumberField = new TextField("Альтернативный тел. номер");
        TextField telegramField = new TextField("Телеграм");
        ComboBox<EmployeeEntity> ifUnavailableComboBox = new ComboBox<>("Если недоступен");

        dialogLayout.add(
                employeeComboBox,
                fullNameField,
                departmentComboBox,
                isManagerCheckbox,
                groupField,
                mainPhoneNumberField,
                altPhoneNumberField,
                telegramField,
                ifUnavailableComboBox
        );

        employeeComboBox.addValueChangeListener(event -> {
            EmployeeEntity selectedEmployee = event.getValue();
            if (selectedEmployee != null) {
                fullNameField.setValue(selectedEmployee.getFullName());
                departmentComboBox.setValue(selectedEmployee.getDepartment());
                isManagerCheckbox.setValue(selectedEmployee.getIsManager());
                groupField.setValue(selectedEmployee.getGroup());
                mainPhoneNumberField.setValue(selectedEmployee.getMainPhoneNumber());
                altPhoneNumberField.setValue(selectedEmployee.getAlternativePhoneNumber());
                telegramField.setValue(selectedEmployee.getTelegram());
                ifUnavailableComboBox.setValue(selectedEmployee.getIfUnavailable());
            } else {
                employeeComboBox.clear();
                fullNameField.clear();
                departmentComboBox.clear();
                isManagerCheckbox.clear();
                groupField.clear();
                mainPhoneNumberField.clear();
                altPhoneNumberField.clear();
                telegramField.clear();
                ifUnavailableComboBox.clear();
            }
        });

        departmentComboBox.addValueChangeListener(event -> {
            DepartmentEntity selectedDepartment = event.getValue();
            if (selectedDepartment != null) {
                ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedDepartment.getId()));
            } else {
                ifUnavailableComboBox.clear();
                ifUnavailableComboBox.setItems();
            }
        });

        Button editButton = new Button("Редактировать", e -> {
            EmployeeEntity selectedEmployee = employeeComboBox.getValue();
            DepartmentEntity selectedDepartment = departmentComboBox.getValue();
            if (selectedEmployee != null && selectedDepartment != null && !fullNameField.isEmpty()) {
                try {
                    EmployeeService.editEmployee(
                            departmentRepository,
                            employeeRepository,
                            selectedEmployee.getId(),
                            fullNameField.getValue(),
                            selectedDepartment,
                            isManagerCheckbox.getValue(),
                            groupField.getValue(),
                            mainPhoneNumberField.getValue(),
                            altPhoneNumberField.getValue(),
                            telegramField.getValue(),
                            ifUnavailableComboBox.getValue()
                    );
                } catch (NotFoundException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                grid.setItems(employeeRepository.findAll(
                        Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
                ));
                ifUnavailableComboBox.clear();
                ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedDepartment.getId()));

                //dialog.close();
            } else {
                Notification.show("Заполните все обязательные поля", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(editButton, cancelButton);

        Button editEmployeeButton = new Button("Редактировать", e -> {
            employeeComboBox.clear();
            employeeComboBox.setItems(setSortedItems(employeeRepository));
            dialog.open();
        });
        editEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(dialog);

        return editEmployeeButton;
    }

    @Transactional
    private Button deleteEmployeeButton(EmployeeRepository employeeRepository,
                                        Grid<EmployeeEntity> grid,
                                        ScheduleRepository scheduleRepository) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Удалить сотрудника");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        ComboBox<EmployeeEntity> employeeComboBox = new ComboBox<>("Сотрудник");
        employeeComboBox.setItems(setSortedItems(employeeRepository));
        employeeComboBox.setItemLabelGenerator(EmployeeEntity::getFullName);
        dialogLayout.add(employeeComboBox);

        Button deleteButton = new Button("Удалить", e -> {
            EmployeeEntity selectedEmployee = employeeComboBox.getValue();
            if (selectedEmployee != null) {
                try {
                    EmployeeService.deleteEmployee(employeeRepository, scheduleRepository, selectedEmployee.getId());
                } catch (NotFoundException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                grid.setItems(employeeRepository.findAll(
                        Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
                ));
                employeeComboBox.setItems(setSortedItems(employeeRepository));
                //dialog.close();
            } else {
                Notification.show("Выберите сотрудника для удаления", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(deleteButton, cancelButton);

        Button deleteEmployeeButton = new Button("Удалить", e -> {
            employeeComboBox.clear();
            employeeComboBox.setItems(setSortedItems(employeeRepository));
            dialog.open();
        });
        deleteEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        add(dialog);

        return deleteEmployeeButton;
    }

    private List<EmployeeEntity> setSortedItems(EmployeeRepository employeeRepository) {
        return employeeRepository.findAll(
                Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
        );
    }
}