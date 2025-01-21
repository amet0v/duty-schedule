package com.nurtel.duty_schedule.schedule.entity;
import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected LocalDate startDate;
    protected LocalDate endDate;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    protected EmployeeEntity employee;
    protected EventTypes event;
}
