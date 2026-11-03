package com.examseatx.model;

import jakarta.persistence.*;

@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Column(nullable = false)
    private int rowCount;

    @Column(nullable = false)
    private int colCount;

    @Column(nullable = false)
    private int benchCapacity; // 1 or 2 students per bench

    @Column(nullable = false)
    private int totalCapacity;

    @Column(nullable = false)
    private boolean cctvAvailable = true;

    @Column(nullable = false)
    private boolean active = true;

    public Classroom() {}

    public Classroom(String roomNumber, int rowCount, int colCount, int benchCapacity, boolean cctvAvailable) {
        this.roomNumber = roomNumber;
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.benchCapacity = benchCapacity;
        this.totalCapacity = rowCount * colCount * benchCapacity;
        this.cctvAvailable = cctvAvailable;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { 
        this.rowCount = rowCount; 
        this.totalCapacity = this.rowCount * this.colCount * this.benchCapacity;
    }

    public int getColCount() { return colCount; }
    public void setColCount(int colCount) { 
        this.colCount = colCount; 
        this.totalCapacity = this.rowCount * this.colCount * this.benchCapacity;
    }

    public int getBenchCapacity() { return benchCapacity; }
    public void setBenchCapacity(int benchCapacity) { 
        this.benchCapacity = benchCapacity; 
        this.totalCapacity = this.rowCount * this.colCount * this.benchCapacity;
    }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public boolean isCctvAvailable() { return cctvAvailable; }
    public void setCctvAvailable(boolean cctvAvailable) { this.cctvAvailable = cctvAvailable; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
