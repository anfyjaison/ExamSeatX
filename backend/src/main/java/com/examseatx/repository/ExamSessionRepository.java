package com.examseatx.repository;

import com.examseatx.model.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    Optional<ExamSession> findBySessionDateAndSessionType(LocalDate sessionDate, String sessionType);
    List<ExamSession> findByActiveTrueOrderBySessionDateDesc();
}
