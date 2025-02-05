package com.nurtel.duty_schedule.schedule.repository;

import com.nurtel.duty_schedule.schedule.entity.EventTypes;
import com.nurtel.duty_schedule.schedule.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    @Query("SELECT s FROM ScheduleEntity s WHERE s.employee.id = :employeeId AND s.event = :eventType")
    List<ScheduleEntity> findEventsByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("eventType") EventTypes eventType
    );

    @Query("SELECT s FROM ScheduleEntity s WHERE s.employee.id = :employeeId")
    List<ScheduleEntity> findAllEventsByEmployee(
            @Param("employeeId") Long employeeId
    );

    @Query("SELECT s FROM ScheduleEntity s WHERE s.employee.id = :employeeId AND :date BETWEEN s.startDate AND s.endDate")
    Optional<ScheduleEntity> findAllEventsByEmployeeAndDate(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date
    );

    @Query("SELECT e FROM ScheduleEntity e WHERE e.startDate <= :endDate AND e.endDate >= :startDate")
    List<ScheduleEntity> findAllByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT s FROM ScheduleEntity s WHERE s.employee.department.id = :departmentId AND :date BETWEEN s.startDate AND s.endDate AND s.event = :eventType")
    Optional<ScheduleEntity> findDutyByDepartmentAndDate(
            @Param("departmentId") Long departmentId,
            @Param("date") LocalDate date,
            @Param("eventType") EventTypes eventType
    );
}
