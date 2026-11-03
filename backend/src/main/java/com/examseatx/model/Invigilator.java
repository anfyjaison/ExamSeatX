package com.examseatx.model;

import jakarta.persistence.*;

@Entity
@Table(name = "invigilators")
public class Invigilator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String facultyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department;

    private String email;

    private String phone;

    @Column(nullable = false)
    private int maxDuties = 5;

    @Column(nullable = false)
    private int assignedDutiesCount = 0;

    @Column(nullable = false)
    private boolean isAvailable = true;

    public Invigilator() {}

    public Invigilator(String facultyId, String name, String department, String email, String phone) {
        this.facultyId = facultyId;
        this.name = name;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.maxDuties = 5;
        this.assignedDutiesCount = 0;
        this.isAvailable = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getMaxDuties() { return maxDuties; }
    public void setMaxDuties(int maxDuties) { this.maxDuties = maxDuties; }

    public int getAssignedDutiesCount() { return assignedDutiesCount; }
    public void setAssignedDutiesCount(int assignedDutiesCount) { this.assignedDutiesCount = assignedDutiesCount; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
