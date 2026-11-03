package com.examseatx.repository;

import com.examseatx.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegisterNumber(String registerNumber);
    List<Student> findBySemesterOrderByRegisterNumberAsc(String semester);
    List<Student> findByDepartmentOrderByRegisterNumberAsc(String department);
    List<Student> findByExamTypeIgnoreCase(String examType);
}
