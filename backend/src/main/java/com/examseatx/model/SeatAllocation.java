package com.examseatx.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_allocations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_session_id", "classroom_id", "row_num", "col_num", "bench_seat_num"})
})
public class SeatAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_session_id", nullable = false)
    private ExamSession examSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Column(name = "row_num", nullable = false)
    private int rowNum; // 1-indexed

    @Column(name = "col_num", nullable = false)
    private int colNum; // 1-indexed

    @Column(name = "bench_seat_num", nullable = false)
    private int benchSeatNum; // 1 for left/single, 2 for right

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private boolean locked = false;

    public SeatAllocation() {}

    public SeatAllocation(ExamSession examSession, Classroom classroom, int rowNum, int colNum, int benchSeatNum, Student student) {
        this.examSession = examSession;
        this.classroom = classroom;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.benchSeatNum = benchSeatNum;
        this.student = student;
        this.locked = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExamSession getExamSession() { return examSession; }
    public void setExamSession(ExamSession examSession) { this.examSession = examSession; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public int getRowNum() { return rowNum; }
    public void setRowNum(int rowNum) { this.rowNum = rowNum; }

    public int getColNum() { return colNum; }
    public void setColNum(int colNum) { this.colNum = colNum; }

    public int getBenchSeatNum() { return benchSeatNum; }
    public void setBenchSeatNum(int benchSeatNum) { this.benchSeatNum = benchSeatNum; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
}
