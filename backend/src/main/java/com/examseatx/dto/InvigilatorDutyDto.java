package com.examseatx.dto;

import java.util.List;

public class InvigilatorDutyDto {
    private Long assignmentId;
    private Long classroomId;
    private String roomNumber;
    private Long invigilatorId;
    private String facultyId;
    private String facultyName;
    private String facultyDepartment;
    private List<String> roomStudentDepartments;
    private int totalStudentsInRoom;
    private String role;
    private boolean departmentNeutral;

    public InvigilatorDutyDto() {}

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Long getInvigilatorId() { return invigilatorId; }
    public void setInvigilatorId(Long invigilatorId) { this.invigilatorId = invigilatorId; }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    public String getFacultyDepartment() { return facultyDepartment; }
    public void setFacultyDepartment(String facultyDepartment) { this.facultyDepartment = facultyDepartment; }

    public List<String> getRoomStudentDepartments() { return roomStudentDepartments; }
    public void setRoomStudentDepartments(List<String> roomStudentDepartments) { this.roomStudentDepartments = roomStudentDepartments; }

    public int getTotalStudentsInRoom() { return totalStudentsInRoom; }
    public void setTotalStudentsInRoom(int totalStudentsInRoom) { this.totalStudentsInRoom = totalStudentsInRoom; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isDepartmentNeutral() { return departmentNeutral; }
    public void setDepartmentNeutral(boolean departmentNeutral) { this.departmentNeutral = departmentNeutral; }
}
