package com.examseatx.repository;

import com.examseatx.model.Invigilator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvigilatorRepository extends JpaRepository<Invigilator, Long> {
    Optional<Invigilator> findByFacultyId(String facultyId);
    List<Invigilator> findByIsAvailableTrueOrderByAssignedDutiesCountAsc();
    List<Invigilator> findByDepartment(String department);
}
