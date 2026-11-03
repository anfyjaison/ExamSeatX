package com.examseatx.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "exam_sessions")
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate sessionDate;

    @Column(nullable = false)
    private String sessionType = "FN"; // FN or AN

    @Column(nullable = false)
    private String examTitle = "KTU B.Tech Degree University Examination";

    private String semester = "S6";

    @Column(nullable = false)
    private boolean active = true;

    public ExamSession() {}

    public ExamSession(LocalDate sessionDate, String sessionType, String examTitle, String semester) {
        this.sessionDate = sessionDate;
        this.sessionType = sessionType;
        this.examTitle = examTitle;
        this.semester = semester;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
