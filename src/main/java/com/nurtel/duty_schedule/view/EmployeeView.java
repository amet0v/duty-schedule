package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
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
import java.util.Objects;
import java.util.Optional;

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
        departmentComboBox.setItems(departmentRepository.findAll());
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
                EmployeeEntity employee = EmployeeEntity.builder()
                        .fullName(fullNameField.getValue())
                        .department(selectedDepartment)
                        .isManager(isManagerCheckbox.getValue())
                        .group(groupField.getValue())
                        .mainPhoneNumber(mainPhoneNumberField.getValue())
                        .alternativePhoneNumber(altPhoneNumberField.getValue())
                        .telegram(telegramField.getValue())
                        .ifUnavailable(ifUnavailableComboBox.getValue())
                        .build();

                Optional<EmployeeEntity> manager = employeeRepository.findManagerByDepartmentId(selectedDepartment.getId());
                if (isManagerCheckbox.getValue() && manager.isPresent()) {
                    Notification.show("У этого отдела уже есть руководитель", 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else {
                    if (isManagerCheckbox.getValue() && manager.isEmpty()) {
                        employee = employeeRepository.save(employee);

                        List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(selectedDepartment.getId());
                        for (EmployeeEntity employeeEntity : employees) {
                            employeeEntity.setManager(employee);
                        }
                        employeeRepository.saveAll(employees);

                    } else if (!isManagerCheckbox.getValue() && manager.isPresent()) {
                        employee.setManager(manager.get());
                        employeeRepository.save(employee);
                    } else if (!isManagerCheckbox.getValue() && manager.isEmpty()) {
                        employeeRepository.save(employee);
                    }
                    grid.setItems(employeeRepository.findAll(
                            Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
                    ));
                    ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedDepartment.getId()));
                    ifUnavailableComboBox.clear();
                }
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
        employeeComboBox.setItems(employeeRepository.findAll());
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
                if (selectedDepartment != selectedEmployee.getDepartment()) {
                    selectedEmployee.setIfUnavailable(null);
                    selectedEmployee.setManager(null);
                    selectedEmployee.setDepartment(selectedDepartment);
                }

                selectedEmployee.setFullName(fullNameField.getValue());
                selectedEmployee.setIsManager(isManagerCheckbox.getValue());
                selectedEmployee.setGroup(groupField.getValue());
                selectedEmployee.setMainPhoneNumber(mainPhoneNumberField.getValue());
                selectedEmployee.setAlternativePhoneNumber(altPhoneNumberField.getValue());
                selectedEmployee.setTelegram(telegramField.getValue());
                selectedEmployee.setIfUnavailable(ifUnavailableComboBox.getValue());

                Optional<EmployeeEntity> manager = employeeRepository.findManagerByDepartmentId(selectedDepartment.getId());
                if (isManagerCheckbox.getValue() && manager.isPresent()) {
                    Notification.show("У этого отдела уже есть руководитель", 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else {
                    if (isManagerCheckbox.getValue() && manager.isEmpty()) {
                        selectedEmployee.setIsManager(isManagerCheckbox.getValue());
                        employeeRepository.save(selectedEmployee);

                        List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(selectedDepartment.getId());
                        for (EmployeeEntity employeeEntity : employees) {
                            employeeEntity.setManager(selectedEmployee);
                        }
                        employeeRepository.saveAll(employees);

                    } else if (!isManagerCheckbox.getValue() && manager.isPresent()) {
                        selectedEmployee.setManager(manager.get());
                        employeeRepository.save(selectedEmployee);
                    } else if (!isManagerCheckbox.getValue() && manager.isEmpty()) {
                        employeeRepository.save(selectedEmployee);
                    }
                    grid.setItems(employeeRepository.findAll(
                            Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
                    ));
                    ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedDepartment.getId()));
                    ifUnavailableComboBox.clear();
                    //dialog.close();
                }
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
            employeeComboBox.setItems(employeeRepository.findAll());
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
        employeeComboBox.setItems(employeeRepository.findAll());
        employeeComboBox.setItemLabelGenerator(EmployeeEntity::getFullName);
        dialogLayout.add(employeeComboBox);

        Button deleteButton = new Button("Удалить", e -> {
            EmployeeEntity selectedEmployee = employeeComboBox.getValue();
            if (selectedEmployee != null) {
                List<ScheduleEntity> scheduleEntityList = scheduleRepository.findAllEventsByEmployee(selectedEmployee.getId());
                scheduleRepository.deleteAll(scheduleEntityList);

                List<EmployeeEntity> employees = employeeRepository.findAllByDepartment(selectedEmployee.getDepartment().getId());
                for (EmployeeEntity entity : employees) {
                    if (selectedEmployee.getIsManager()) entity.setManager(null);
                    if (entity.getIfUnavailable() != null && Objects.equals(entity.getIfUnavailable().getId(), selectedEmployee.getId()))
                        entity.setIfUnavailable(null);
                }
                employeeRepository.saveAll(employees);

                selectedEmployee.setIfUnavailable(null);
                selectedEmployee.setManager(null);
                selectedEmployee.setDepartment(null);

                employeeRepository.save(selectedEmployee);
                employeeRepository.delete(selectedEmployee);

                grid.setItems(employeeRepository.findAll(
                        Sort.by(Sort.Order.asc("department.id"), Sort.Order.desc("isManager"), Sort.Order.asc("fullName"))
                ));
                employeeComboBox.setItems(employeeRepository.findAll());
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
            employeeComboBox.setItems(employeeRepository.findAll());
            dialog.open();
        });
        deleteEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        add(dialog);

        return deleteEmployeeButton;
    }
}