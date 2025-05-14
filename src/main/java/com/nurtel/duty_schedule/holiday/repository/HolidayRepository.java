package com.nurtel.duty_schedule.holiday.repository;

import com.nurtel.duty_schedule.holiday.entity.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {
    List<HolidayEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
