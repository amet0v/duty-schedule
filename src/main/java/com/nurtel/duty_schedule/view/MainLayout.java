package com.nurtel.duty_schedule.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

//@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        // Верхняя панель
        createHeader();

        // Боковая панель
        createSidebar();
    }

    private void createHeader() {
        H1 logo = new H1("NurTelecom");
        logo.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-xl)")
                //.set("background-color", "#ef107f")
                .set("color", "#ffffff");

        Button logoutButton = new Button("Выйти");

        HorizontalLayout header = new HorizontalLayout(logo, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("background-color", "#ef107f")
                .set("color", "##000000");

        addToNavbar(header);
    }

    private void createSidebar() {
        // Ссылки на страницы
        Icon departmentIcon = new Icon(VaadinIcon.GROUP);
        departmentIcon.setSize("16px");
        RouterLink departmentLink = new RouterLink(DepartmentView.class);
        HorizontalLayout departmentLayout = new HorizontalLayout(departmentIcon, new Text("Отделы"));
        departmentLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        departmentLink.add(departmentLayout);

        Icon employeeIcon = new Icon(VaadinIcon.USER);
        employeeIcon.setSize("16px");
        RouterLink employeeLink = new RouterLink(EmployeeView.class);
        HorizontalLayout employeeLayout = new HorizontalLayout(employeeIcon, new Text("Сотрудники"));
        employeeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        employeeLink.add(employeeLayout);

        Icon scheduleIcon = new Icon(VaadinIcon.CALENDAR);
        scheduleIcon.setSize("16px");
        RouterLink scheduleLink = new RouterLink(ScheduleView.class);// Иконка для "Расписание"
        HorizontalLayout scheduleLayout = new HorizontalLayout(scheduleIcon, new Text("Расписание"));
        scheduleLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        scheduleLink.add(scheduleLayout);

        // Устанавливаем общий стиль для ссылок
        departmentLink.getStyle().set("color", "#b8c7ce")
                .set("font-weight", "400")
                .set("font-family", "'Montserrat', sans-serif")
                .set("font-size", "14px")
                .set("margin-bottom", "10px");
        employeeLink.getStyle().set("color", "#b8c7ce")
                .set("font-weight", "400")
                .set("font-family", "'Montserrat', sans-serif")
                .set("font-size", "14px")
                .set("margin-bottom", "10px");
        scheduleLink.getStyle().set("color", "#b8c7ce")
                .set("font-weight", "400")
                .set("font-family", "'Montserrat', sans-serif")
                .set("font-size", "14px")
                .set("margin-bottom", "10px");

        VerticalLayout sidebar = new VerticalLayout(departmentLink, employeeLink, scheduleLink);
        sidebar.setSpacing(true);
        sidebar.setPadding(true);
        sidebar.getStyle()
                .set("background-color", "#232b33")
                .set("color", "#b8c7ce")
                .set("border-right", "1px solid var(--lumo-contrast-10pct)")
                .set("height", "100vh");

        addToDrawer(sidebar);
    }
}
