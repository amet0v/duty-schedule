package com.nurtel.duty_schedule.employee.entity;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employees")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long Id;
    protected String fullName;
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    protected DepartmentEntity department;
    protected Boolean isManager;
    protected String group;
    protected String mainPhoneNumber;
    protected String alternativePhoneNumber;
    protected String telegram;
    @ManyToOne
    @JoinColumn(name = "if_unavailable_id")
    protected EmployeeEntity ifUnavailable;
    protected Date lastCallDate;
}
