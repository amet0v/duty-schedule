package com.nurtel.duty_schedule.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class HomeView extends Div {
    public HomeView() {
        add("Добро пожаловать на главную страницу!");
    }
}

//@Route(value = "about", layout = MainLayout.class)
//public class AboutView extends Div {
//    public AboutView() {
//        add("Информация о нас.");
//    }
//}
