package com.examseatx.repository;

import com.examseatx.model.Classroom;
import com.examseatx.model.ExamSession;
import com.examseatx.model.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {
    List<SeatAllocation> findByExamSessionOrderByClassroomRoomNumberAscRowNumAscColNumAscBenchSeatNumAsc(ExamSession examSession);
    List<SeatAllocation> findByExamSessionAndClassroomOrderByRowNumAscColNumAscBenchSeatNumAsc(ExamSession examSession, Classroom classroom);
    void deleteByExamSession(ExamSession examSession);
    long countByExamSession(ExamSession examSession);
}
