package com.nurtel.duty_schedule.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLink;

import java.util.ArrayList;
import java.util.List;

//@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createSidebar();
    }

    private void createHeader() {
        H1 logo = new H1("О! НурТелеком");
        logo.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("color", "#ffffff");

        Button logoutButton = new Button("Выйти");

        HorizontalLayout header = new HorizontalLayout(logo, logoutButton);
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
    }

    private void createSidebar() {
        SideNav sideNav = new SideNav();

        Icon departmentIcon = VaadinIcon.GROUP.create();
        departmentIcon.setColor("#b8c7ce"); // Устанавливаем цвет иконки
        SideNavItem departmentItem = new SideNavItem("Отделы", DepartmentView.class, departmentIcon);

        Icon employeeIcon = VaadinIcon.USER.create();
        employeeIcon.setColor("#b8c7ce"); // Устанавливаем цвет иконки
        SideNavItem employeeItem = new SideNavItem("Сотрудники", EmployeeView.class, employeeIcon);

        Icon scheduleIcon = VaadinIcon.CALENDAR.create();
        scheduleIcon.setColor("#b8c7ce"); // Устанавливаем цвет иконки
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