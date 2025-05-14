package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import com.nurtel.duty_schedule.department.repository.DepartmentRepository;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.employee.repository.EmployeeRepository;
import com.nurtel.duty_schedule.employee.service.EmployeeService;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.user.entity.UserEntity;
import com.nurtel.duty_schedule.user.repository.UserRepository;
import com.nurtel.duty_schedule.user.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MainLayout extends AppLayout {
    @Autowired
    private AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Button loginButton;
    private MenuBar logoutBar;
    private MenuItem usernameItem;
    private final Integer sessionInterval = 3600;
    private Button editMyProfileButton;

    public MainLayout(UserRepository userRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        createHeader(userRepository, employeeRepository, departmentRepository);
        createSidebar();
    }

    private Optional<UserEntity> authenticate(UserRepository userRepository, String username, String password) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) return user;
        }
        return Optional.empty();
    }

    public static boolean isAuthenticated() {
        return VaadinSession.getCurrent().getAttribute("username") != null;
    }

    public static boolean isManager() {
        return VaadinSession.getCurrent().getAttribute("role") == "manager";
    }

    public static boolean isInDepartment(DepartmentEntity department) {
        return Objects.equals(VaadinSession.getCurrent().getAttribute("department").toString(), department.getId().toString());
    }

    public static boolean setRole(EmployeeRepository employeeRepository, String username) {
        Optional<EmployeeEntity> employee = employeeRepository.findByLogin(username);
        return employee.map(employeeEntity -> employeeEntity.getIsManager().equals(true)).orElse(false);
    }

    public static String setDepartment(EmployeeRepository employeeRepository, String username) {
        Optional<EmployeeEntity> employee = employeeRepository.findByLogin(username);
        return employee.map(employeeEntity -> employeeEntity.getDepartment().getId().toString()).orElse(null);
    }

    private void updateButtonsVisibility() {
        UI.getCurrent().access(() -> {
            boolean auth = isAuthenticated();
            boolean role = isManager();
            loginButton.setVisible(!auth);
            logoutBar.setVisible(auth);

            String username = (String) VaadinSession.getCurrent().getAttribute("username");
            if (username == null) {
                username = "";
            }
            updateLogoutBar(username);
            
            DepartmentView.addButton.setVisible(auth && role);
            DepartmentView.editButton.setVisible(auth && role);
            DepartmentView.deleteButton.setVisible(auth && role);

            EmployeeView.addButton.setVisible(auth && role);
            EmployeeView.editButton.setVisible(auth && role);
            EmployeeView.deleteButton.setVisible(auth && role);

            editMyProfileButton.setVisible(auth);
        });
    }

    private void createHeader(UserRepository userRepository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        StreamResource imageResource = new StreamResource("logo_o.svg",
                () -> getClass().getResourceAsStream("/images/logo_o.svg"));

        Image image = new Image(imageResource, "My Streamed Image");
        image.setWidth("70px");
        image.setHeight("70px");

        H1 logo = new H1("«NUR Telecom» LLC");
        logo.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("color", "#ffffff");

        HorizontalLayout nurLogo = new HorizontalLayout();
        nurLogo.setSpacing(false);
        nurLogo.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        nurLogo.add(image, logo);

        boolean isAuthenticated = VaadinSession.getCurrent().getSession().getAttribute("username") != null;

        Dialog loginDialog = new Dialog();
        loginDialog.setHeaderTitle("Вход в систему");
        TextField usernameField = new TextField("Логин");
        PasswordField passwordField = new PasswordField("Пароль");

        VerticalLayout loginDialogLayout = new VerticalLayout();
        loginDialogLayout.add(usernameField, passwordField);

        loginDialog.add(loginDialogLayout);

        Button dialogLoginButton = new Button("Войти", e -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();

            /*
            Optional<UserEntity> user = authenticate(userRepository, username, password);
            if (user.isPresent()) {
                VaadinSession.getCurrent().setAttribute("user", user.get());

                loginDialog.close();
                String currentRoute = UI.getCurrent().getInternals().getActiveViewLocation().getPath();
                if (currentRoute.equals("schedule"))
                    UI.getCurrent().getPage().reload();
                else updateButtonsVisibility();

                Notification.show("Сессия установлена для пользователя: " + username, 5000, Notification.Position.BOTTOM_END);
            } else {
                Notification.show("Неверные учетные данные", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            */

            //LDAP start
            try {
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );

                VaadinSession.getCurrent().setAttribute("username", username);
                VaadinSession.getCurrent().setAttribute("role", setRole(employeeRepository, username) ? "manager" : "user");
                VaadinSession.getCurrent().setAttribute("department", setDepartment(employeeRepository, username));

                WrappedSession wrappedSession = VaadinSession.getCurrent().getSession();
                wrappedSession.setMaxInactiveInterval(sessionInterval);

                loginDialog.close();

                String currentRoute = UI.getCurrent().getInternals().getActiveViewLocation().getPath();
                if (currentRoute.equals("schedule"))
                    UI.getCurrent().getPage().reload();
                else updateButtonsVisibility();
                UI.getCurrent().getPage().reload();

                Notification.show("Сессия установлена для пользователя: " + username, 5000, Notification.Position.BOTTOM_END);
            } catch (AuthenticationException ex) {
                Notification.show("Неверные учетные данные", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            //LDAP END
        });
        dialogLoginButton.addClickShortcut(Key.ENTER);

        Button loginDialogCancelButton = new Button("Отмена", e -> loginDialog.close());
        loginDialogCancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        loginDialog.getFooter().add(dialogLoginButton, loginDialogCancelButton);

        loginButton = new Button("Войти", e -> {
            usernameField.clear();
            passwordField.clear();
            loginDialog.open();
        });
        loginButton.setVisible(!isAuthenticated);
        loginButton.getStyle()
                .set("background-color", "#ff2898")
                .set("color", "#ffffff");


        Button logoutButton = new Button("Выйти", VaadinIcon.POWER_OFF.create(), e -> {
            VaadinSession.getCurrent().getSession().invalidate();
            VaadinSession.getCurrent().close();
            Notification.show("Вы вышли из учетной записи", 3000, Notification.Position.BOTTOM_END);
            updateButtonsVisibility();
        });
        logoutButton.getStyle()
                .set("background-color", "#ff2898")
                .set("color", "#ffffff");

        Dialog createUserDialog = new Dialog();
        createUserDialog.setHeaderTitle("Новый пользователь");
        TextField newUserUsernameField = new TextField("Логин");
        PasswordField newUserPasswordField = new PasswordField("Пароль");

        VerticalLayout createUserLayout = new VerticalLayout();
        createUserLayout.add(newUserUsernameField, newUserPasswordField);
        createUserDialog.add(createUserLayout);

        Button createUserDialogButton = new Button("Создать", e -> {

            if (!newUserUsernameField.isEmpty() && !newUserPasswordField.isEmpty()) {
                try {
                    UserService.createUser(
                            userRepository,
                            newUserUsernameField.getValue(),
                            newUserPasswordField.getValue(),
                            passwordEncoder
                    );
                    Notification.show("Пользователь " + newUserUsernameField.getValue() + " успешно создан",
                                    5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (BadRequestException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Заполните все обязательные поля", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        Button createUserDialogCancelButton = new Button("Отмена", e -> createUserDialog.close());
        createUserDialogCancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        createUserDialog.getFooter().

                add(createUserDialogButton, createUserDialogCancelButton);

        Button addUserButton = new Button("Добавить пользователя", VaadinIcon.CLIPBOARD_USER.create(), e -> {
            newUserPasswordField.clear();
            newUserUsernameField.clear();
            createUserDialog.open();
        });
        addUserButton.getStyle()
                .set("background-color", "#ffffff")
                .set("color", "#000000");

        logoutBar = new MenuBar();

        Icon userIcon = new Icon(VaadinIcon.USER);

        usernameItem = logoutBar.addItem(userIcon);
        usernameItem.getStyle()
                .set("background-color", "#ff2898")
                .set("color", "#ffffff");

        //usernameItem.getSubMenu().addItem(addUserButton);
        editMyProfileButton = editMyProfileButton(employeeRepository, departmentRepository);
        usernameItem.getSubMenu().addItem(editMyProfileButton);

        usernameItem.getSubMenu().addItem(logoutButton);
        logoutBar.setVisible(isAuthenticated);
        logoutBar.getStyle()
                .set("background-color", "#ff2898")
                .set("color", "#ffffff");

        HorizontalLayout header = new HorizontalLayout(nurLogo, loginButton, logoutBar);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(true);

        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        header.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("background-color", "#ef107f")
                .set("color", "#000000")
                .set("padding", "0.2%")
                //.set("padding-bottom", "0.5%")
                .set("padding-right", "1%")
                .set("margin", "0");

        addToNavbar(header);

        updateButtonsVisibility();
    }

    private void updateLogoutBar(String username) {
        usernameItem.add(username);

        logoutBar.setVisible(isAuthenticated());
    }

    private void createSidebar() {
        SideNav sideNav = new SideNav();

        Icon departmentIcon = VaadinIcon.GROUP.create();
        departmentIcon.setColor("#b8c7ce");
        SideNavItem departmentItem = new SideNavItem("Отделы", DepartmentView.class, departmentIcon);

        Icon employeeIcon = VaadinIcon.USER.create();
        employeeIcon.setColor("#b8c7ce");
        SideNavItem employeeItem = new SideNavItem("Сотрудники", EmployeeView.class, employeeIcon);

        Icon scheduleIcon = VaadinIcon.CALENDAR.create();
        scheduleIcon.setColor("#b8c7ce");
        SideNavItem scheduleItem = new SideNavItem("Расписание", ScheduleView.class, scheduleIcon);

        Icon archiveIcon = VaadinIcon.ARCHIVE.create();
        archiveIcon.setColor("#b8c7ce");
        SideNavItem archiveItem = new SideNavItem("Архив", ArchiveView.class, archiveIcon);

        Icon holidaysIcon = VaadinIcon.GIFT.create();
        holidaysIcon.setColor("#b8c7ce");
        SideNavItem holidaysItem = new SideNavItem("Праздники", HolidayView.class, holidaysIcon);

        List<SideNavItem> sideNavItems = List.of(departmentItem, employeeItem, scheduleItem, archiveItem, holidaysItem);

        for (SideNavItem item : sideNavItems) {
            item.getStyle()
                    .set("color", "#b8c7ce")
                    .set("font-size", "14px")
                    .set("font-weight", "400")
                    .set("padding-bottom", "10px")
                    .set("padding-top", "10px");

            item.addAttachListener(event -> {
                item.getElement().addEventListener("mouseover", e ->
                        item.getElement().getStyle()
                                .set("background-color", "#394247")
                                .set("color", "#ffffff")
                );
                item.getElement().addEventListener("mouseout", e ->
                        item.getElement().getStyle()
                                .set("background-color", "")
                                .set("color", "#b8c7ce")
                );
            });
        }

        sideNav.getStyle()
                .set("background-color", "#232b33");

        sideNav.addItem(departmentItem, employeeItem, scheduleItem, archiveItem, holidaysItem);
        addToDrawer(sideNav);

        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setSpacing(true);
        sidebar.setPadding(true);
        sidebar.getStyle()
                .set("background-color", "#232b33")
                .set("color", "#b8c7ce")
                .set("height", "100vh");

        addToDrawer(sidebar);
    }

    private Button editMyProfileButton(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository
    ) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Редактировать информацию о себе");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);

        String authorizedEmployee = (String) VaadinSession.getCurrent().getAttribute("username");
        Optional<EmployeeEntity> selectedEmployeeOptional = employeeRepository.findByLogin(authorizedEmployee);
        if (selectedEmployeeOptional.isEmpty()) return new Button("empty");
        EmployeeEntity selectedEmployee = selectedEmployeeOptional.get();

        TextField fullNameField = new TextField("Фамилия Имя");
        fullNameField.setValue(selectedEmployee.getFullName());

        ComboBox<DepartmentEntity> departmentComboBox = new ComboBox<>("Отдел");
        departmentComboBox.setItems(departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        departmentComboBox.setItemLabelGenerator(DepartmentEntity::getName);
        departmentComboBox.setValue(selectedEmployee.getDepartment());

        TextField groupField = new TextField("Группа");
        groupField.setValue(selectedEmployee.getGroup() != null ? selectedEmployee.getGroup() : "");

        TextField mainPhoneNumberField = new TextField("Основной тел. номер");
        mainPhoneNumberField.setValue(selectedEmployee.getMainPhoneNumber()!= null ? selectedEmployee.getMainPhoneNumber() : "");

        TextField altPhoneNumberField = new TextField("Альтернативный тел. номер");
        altPhoneNumberField.setValue(selectedEmployee.getAlternativePhoneNumber()!= null ? selectedEmployee.getAlternativePhoneNumber() : "");

        TextField telegramField = new TextField("Телеграм");
        telegramField.setValue(selectedEmployee.getTelegram()!= null ? selectedEmployee.getTelegram() : "");

        ComboBox<EmployeeEntity> ifUnavailableComboBox = new ComboBox<>("Если недоступен");
        ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedEmployee.getDepartment().getId()));
        ifUnavailableComboBox.setValue(selectedEmployee.getIfUnavailable());

        dialogLayout.add(
                fullNameField,
                departmentComboBox,
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

        Button editButton = new Button("Редактировать", e -> {
            DepartmentEntity selectedDepartment = departmentComboBox.getValue();
            if (selectedDepartment != null && !fullNameField.isEmpty()) {
                try {
                    EmployeeService.editEmployee(
                            departmentRepository,
                            employeeRepository,
                            selectedEmployee.getId(),
                            fullNameField.getValue(),
                            selectedDepartment,
                            selectedEmployee.getIsManager(),
                            groupField.getValue(),
                            mainPhoneNumberField.getValue(),
                            altPhoneNumberField.getValue(),
                            telegramField.getValue(),
                            null,
                            ifUnavailableComboBox.getValue(),
                            null
                    );
                    Notification.show(String.format(
                                    "Сотрудник \"%s\" успешно изменен", fullNameField.getValue()), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS
                            );
                } catch (NotFoundException ex) {
                    Notification.show(ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                ifUnavailableComboBox.clear();
                ifUnavailableComboBox.setItems(employeeRepository.findAllByDepartment(selectedDepartment.getId()));

                dialog.close();
            } else {
                Notification.show("Заполните все обязательные поля", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(editButton, cancelButton);

        Button editEmployeeButton = new Button("Мой профиль", VaadinIcon.USER_CARD.create(), e -> {
            dialog.open();
        });
        editEmployeeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        return editEmployeeButton;
    }
}