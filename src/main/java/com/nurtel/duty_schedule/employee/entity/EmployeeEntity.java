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
    protected Long id;
    protected String fullName;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "department_id", nullable = false)
    protected DepartmentEntity department;
    protected Boolean isManager;
    @Column(name = "employee_group")
    protected String group;
    protected String mainPhoneNumber;
    protected String alternativePhoneNumber;
    protected String telegram;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "if_unavailable_id",  nullable = true)
    protected EmployeeEntity ifUnavailable;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_id",  nullable = true)
    protected EmployeeEntity manager;
    protected Date lastCallDate;

    @Override
    public String toString(){
        return this.fullName;
    }
}
