package com.examseatx.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "duty_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_session_id", "classroom_id"})
})
public class DutyAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_session_id", nullable = false)
    private ExamSession examSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invigilator_id", nullable = false)
    private Invigilator invigilator;

    private String role = "Chief Invigilator";

    private LocalDateTime assignedAt = LocalDateTime.now();

    public DutyAssignment() {}

    public DutyAssignment(ExamSession examSession, Classroom classroom, Invigilator invigilator, String role) {
        this.examSession = examSession;
        this.classroom = classroom;
        this.invigilator = invigilator;
        this.role = role != null ? role : "Chief Invigilator";
        this.assignedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExamSession getExamSession() { return examSession; }
    public void setExamSession(ExamSession examSession) { this.examSession = examSession; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public Invigilator getInvigilator() { return invigilator; }
    public void setInvigilator(Invigilator invigilator) { this.invigilator = invigilator; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
