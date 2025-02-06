package com.nurtel.duty_schedule.view;

import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import com.nurtel.duty_schedule.schedule.repository.ScheduleRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Route(value = "/archive", layout = MainLayout.class)
@PageTitle("Архив")
public class ArchiveView extends VerticalLayout {

    public ArchiveView(ScheduleRepository scheduleRepository) {
        Map<YearMonth, List<ScheduleEntity>> schedulesByYearMonth = scheduleRepository.findAll().stream()
                .collect(Collectors.groupingBy(schedule -> YearMonth.from(schedule.getStartDate())));

        schedulesByYearMonth.forEach((yearMonth, schedules) -> {
            String monthName = yearMonth.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"));
            add(new com.vaadin.flow.component.html.H3("Архив за " + monthName + " " + yearMonth.getYear()));
            Grid<ScheduleEntity> scheduleEntityGrid = new Grid<>(ScheduleEntity.class);

            scheduleEntityGrid.removeAllColumns();

            scheduleEntityGrid.addColumn(ScheduleEntity::getId).setHeader("ID");
            scheduleEntityGrid.addColumn(schedule -> schedule.getEmployee() != null
                            ? schedule.getEmployee().getFullName()
                            : "❌ Удаленный сотрудник")
                    .setHeader("Сотрудник")
                    .setSortable(true);
            scheduleEntityGrid.addColumn(schedule -> schedule.getEvent() == EventTypes.Duty
                            ? "\uD83D\uDEE0\uFE0F Дежурство"
                            : "\uD83C\uDFD6\uFE0F Отпуск")
                    .setHeader("Тип события")
                    .setSortable(true);
            scheduleEntityGrid.addColumn(ScheduleEntity::getStartDate)
                    .setHeader("Дата начала")
                    .setSortable(true);
            scheduleEntityGrid.addColumn(ScheduleEntity::getEndDate)
                    .setHeader("Дата окончания")
                    .setSortable(true);

            scheduleEntityGrid.setItems(schedules);
            add(scheduleEntityGrid);
        });
    }
}
