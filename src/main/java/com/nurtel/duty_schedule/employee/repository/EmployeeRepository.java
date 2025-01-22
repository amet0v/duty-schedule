package com.nurtel.duty_schedule.employee.repository;

import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query("SELECT e FROM EmployeeEntity e WHERE e.department.id = :departmentId ORDER BY e.lastCallDate ASC, e.id ASC")
    List<EmployeeEntity> findTopByDepartmentIdOrderByLastCallDateAsc(@Param("departmentId") Long departmentId);
}
