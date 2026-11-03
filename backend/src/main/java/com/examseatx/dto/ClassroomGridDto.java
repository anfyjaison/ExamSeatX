package com.examseatx.dto;

public class ClassroomGridDto {
    private int rowNum;
    private int colNum;
    private int benchSeatNum; // 1 or 2
    private String seatLabel; // e.g. "R1-C1-S1"
    private Long studentId;
    private String registerNumber;
    private String studentName;
    private String department;
    private String branchCode;
    private String subjectCode;
    private String subjectName;
    private String examType;
    private boolean conflict;
    private String conflictReason;

    public ClassroomGridDto() {}

    public int getRowNum() { return rowNum; }
    public void setRowNum(int rowNum) { this.rowNum = rowNum; }

    public int getColNum() { return colNum; }
    public void setColNum(int colNum) { this.colNum = colNum; }

    public int getBenchSeatNum() { return benchSeatNum; }
    public void setBenchSeatNum(int benchSeatNum) { this.benchSeatNum = benchSeatNum; }

    public String getSeatLabel() { return seatLabel; }
    public void setSeatLabel(String seatLabel) { this.seatLabel = seatLabel; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public boolean isConflict() { return conflict; }
    public void setConflict(boolean conflict) { this.conflict = conflict; }

    public String getConflictReason() { return conflictReason; }
    public void setConflictReason(String conflictReason) { this.conflictReason = conflictReason; }
}
