package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.holiday.entity.HolidayEntity;
import com.nurtel.duty_schedule.holiday.repository.HolidayRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.List;

@Route(value = "/holidays", layout = MainLayout.class)
@PageTitle("Праздники")
public class HolidayView extends VerticalLayout {
    public HolidayView(HolidayRepository holidayRepository) {

        boolean isVisible = MainLayout.isAuthenticated() && MainLayout.isManager();

        DatePicker datePicker = new DatePicker("Выберите дату");
        datePicker.setI18n(new DatePicker.DatePickerI18n().setFirstDayOfWeek(1));
        datePicker.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            if (selectedDate != null) {
                System.out.println("Выбрана дата: " + selectedDate);
            }
        });

        datePicker.setVisible(isVisible);
        add(datePicker);

        Grid<HolidayEntity> holidayEntityGrid = new Grid<>(HolidayEntity.class);
        holidayEntityGrid.getStyle().set("height", "80vh");

        List<HolidayEntity> holidays = holidayRepository.findAll();
        holidayEntityGrid.setItems(holidays);

        Button addHoliday = new Button("Добавить выходной", e -> {
            System.out.println("added date: " + datePicker.getValue());
            if (datePicker.getValue() != null) {
                HolidayEntity holiday = HolidayEntity.builder()
                        .date(datePicker.getValue())
                        .build();
                holidayRepository.save(holiday);
                List<HolidayEntity> newHolidays = holidayRepository.findAll();
                holidayEntityGrid.setItems(newHolidays);
            }
        });
        addHoliday.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addHoliday.setVisible(isVisible);

        add(addHoliday);

        holidayEntityGrid.removeAllColumns();

        holidayEntityGrid.addColumn(HolidayEntity::getDate).setHeader("Дата");
        holidayEntityGrid.addComponentColumn(holiday -> {
            Button deleteButton = new Button("\uD83D\uDDD1\uFE0F");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(click -> {
                holidayRepository.delete(holiday);
                List<HolidayEntity> newHolidays = holidayRepository.findAll();
                holidayEntityGrid.setItems(newHolidays);
            });
            deleteButton.setVisible(isVisible);
            return deleteButton;
        }).setHeader("Удалить").setAutoWidth(true);

        add(holidayEntityGrid);
    }
}
