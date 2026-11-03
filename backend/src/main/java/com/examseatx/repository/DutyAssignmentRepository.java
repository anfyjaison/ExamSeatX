package com.examseatx.repository;

import com.examseatx.model.Classroom;
import com.examseatx.model.DutyAssignment;
import com.examseatx.model.ExamSession;
import com.examseatx.model.Invigilator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DutyAssignmentRepository extends JpaRepository<DutyAssignment, Long> {
    List<DutyAssignment> findByExamSessionOrderByClassroomRoomNumberAsc(ExamSession examSession);
    Optional<DutyAssignment> findByExamSessionAndClassroom(ExamSession examSession, Classroom classroom);
    List<DutyAssignment> findByInvigilator(Invigilator invigilator);
    void deleteByExamSession(ExamSession examSession);
}
