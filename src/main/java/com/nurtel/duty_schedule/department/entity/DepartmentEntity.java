package com.nurtel.duty_schedule.department.entity;

import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "departments")
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected String name;
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    protected List<EmployeeEntity> employees;
}
