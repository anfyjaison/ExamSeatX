package com.examseatx.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String registerNumber;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String department; // e.g. "Computer Science", "Artificial Intelligence and Data Science"

    @Column(nullable = false)
    private String branchCode; // e.g. "CS", "AD", "EC", "EE", "IT", "ME", "CE", "CSD"

    @Column(nullable = false)
    private String semester; // e.g. "S6", "S4", "S2"

    @Column(nullable = false)
    private String subjectCode; // e.g. "CST304", "AIT304"

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private String examType = "Regular"; // "Regular" or "Supplementary"

    private String slot; // e.g. "SLOT B", "SLOT C"

    public Student() {}

    public Student(String registerNumber, String studentName, String department, String branchCode, 
                   String semester, String subjectCode, String subjectName, String examType, String slot) {
        this.registerNumber = registerNumber;
        this.studentName = studentName;
        this.department = department;
        this.branchCode = branchCode;
        this.semester = semester;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.examType = examType != null && !examType.trim().isEmpty() ? examType : "Regular";
        this.slot = slot;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
}
