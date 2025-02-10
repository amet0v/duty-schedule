package com.nurtel.duty_schedule.department.entity;

import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
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
    protected String title;
    protected Integer number;
    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    protected List<EmployeeEntity> employees;

    @Override
    public String toString() {
        return this.getTitle() != null
                ? this.getTitle().isBlank()
                    ? this.getName()
                    : String.format(("%s (%s)"), this.getTitle(), this.getName())
                : this.getName();
    }

    public List<EmployeeEntity> getEmployees() {
        return employees != null ? employees : Collections.emptyList();
    }

}
