package com.nurtel.duty_schedule.department.repository;

import com.nurtel.duty_schedule.department.entity.DepartmentEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    Optional<DepartmentEntity> findByName(String name);

    @Query("SELECT d FROM DepartmentEntity d WHERE d.number IS NOT NULL ORDER BY d.number ASC")
    List<DepartmentEntity> findAllWithNumber();
}
