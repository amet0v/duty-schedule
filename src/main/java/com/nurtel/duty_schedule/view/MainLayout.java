package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.user.entity.UserEntity;
import com.nurtel.duty_schedule.user.repository.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

public class MainLayout extends AppLayout {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Button loginButton;
    private MenuBar logoutBar;

    public MainLayout(UserRepository userRepository) {
        createHeader(userRepository);
        createSidebar();

        VaadinSession.getCurrent().getService().addSessionInitListener((SessionInitListener) event -> {
            UI.getCurrent().access(() -> UI.getCurrent().getPage().reload());
        });

        VaadinSession.getCurrent().getService().addSessionDestroyListener((SessionDestroyListener) event -> {
            UI.getCurrent().access(() -> UI.getCurrent().getPage().reload());
        });
    }

    private Optional<UserEntity> authenticate(UserRepository userRepository, String username, String password){
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) return user;
        }
        return Optional.empty();
    }

    public static boolean isAuthenticated() {
        return VaadinSession.getCurrent().getAttribute("user") != null;
    }

    private void updateButtonsVisibility() {
        UI.getCurrent().access(() -> {
            boolean auth = isAuthenticated();
            loginButton.setVisible(!auth);
            logoutBar.setVisible(auth);

            DepartmentView.addButton.setVisible(auth);
            DepartmentView.editButton.setVisible(auth);
            DepartmentView.deleteButton.setVisible(auth);

            EmployeeView.addButton.setVisible(auth);
            EmployeeView.editButton.setVisible(auth);
            EmployeeView.deleteButton.setVisible(auth);
        });
    }

    private void createHeader(UserRepository userRepository) {
        H1 logo = new H1("О! НурТелеком");
        logo.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("color", "#ffffff");

        boolean isAuthenticated = VaadinSession.getCurrent().getSession().getAttribute("user") != null;

        Dialog loginDialog = new Dialog();
        loginDialog.setHeaderTitle("Вход в систему");
        TextField loginField = new TextField("Логин");
        TextField passwordField = new TextField("Пароль");

        VerticalLayout loginDialogLayout = new VerticalLayout();
        loginDialogLayout.add(loginField, passwordField);
        
        loginDialog.add(loginDialogLayout);

        Button dialogLoginButton = new Button("Войти", e -> {
            String username = loginField.getValue();
            String password = passwordField.getValue();

            Optional<UserEntity> user = authenticate(userRepository, username, password);
            if (user.isPresent()) {
                VaadinSession.getCurrent().setAttribute("user", user);
                Notification.show("Сессия установлена для пользователя: " + username, 5000, Notification.Position.BOTTOM_END);

                String currentRoute = UI.getCurrent().getInternals().getActiveViewLocation().getPath();
                loginDialog.close();
                if (currentRoute.equals("schedule")) {
                    UI.getCurrent().getPage().reload();
                } else updateButtonsVisibility();
            } else {
                Notification.show("Неверные учетные данные", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button dialogCncelButton = new Button("Отмена", e -> loginDialog.close());
        dialogCncelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        loginDialog.getFooter().add(dialogLoginButton, dialogCncelButton);

        loginButton = new Button("Войти", e -> {
            loginField.clear();
            passwordField.clear();
            loginDialog.open();
        });
        loginButton.setVisible(!isAuthenticated);
        loginButton.getStyle()
                .set("background-color" , "#ff2898")
                .set("color", "#ffffff");


        Button logoutButton = new Button("Выйти", VaadinIcon.POWER_OFF.create() ,e -> {
            VaadinSession.getCurrent().getSession().invalidate();
            VaadinSession.getCurrent().close();
            Notification.show("Вы вышли из учетной записи", 3000, Notification.Position.BOTTOM_END);
            updateButtonsVisibility();
        });

        logoutButton.getStyle()
                .set("background-color" , "#ff2898")
                .set("color", "#ffffff");

        Button addUserButton = new Button("add user", VaadinIcon.CLIPBOARD_USER.create());
        addUserButton.getStyle()
                .set("background-color" , "#ffffff")
                .set("color", "#000000");

        logoutBar = new MenuBar();
        Icon userIcon = new Icon(VaadinIcon.USER);

        MenuItem usernameItem = logoutBar.addItem(userIcon);
        usernameItem.add("username");
        usernameItem.getStyle()
                .set("background-color" , "#ff2898")
                .set("color", "#ffffff");

        usernameItem.getSubMenu().addItem(addUserButton);

        usernameItem.getSubMenu().addItem(logoutButton);
        logoutBar.setVisible(isAuthenticated);
        logoutBar.getStyle()
                .set("background-color" , "#ff2898")
                .set("color", "#ffffff");

        HorizontalLayout header = new HorizontalLayout(logo, loginButton, logoutBar);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);

        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        header.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("background-color", "#ef107f")
                .set("color", "#000000");

        addToNavbar(header);

        updateButtonsVisibility();
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

        List<SideNavItem> sideNavItems = List.of(departmentItem, employeeItem, scheduleItem);

        for (SideNavItem item : sideNavItems){
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

        sideNav.addItem(departmentItem, employeeItem, scheduleItem);
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
}