package com.examseatx.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomSeatingMatrixDto {
    private Long roomId;
    private String roomNumber;
    private int rowCount;
    private int colCount;
    private int benchCapacity;
    private int totalCapacity;
    private int occupiedSeats;
    private boolean cctvAvailable;
    private String invigilatorName;
    private String invigilatorDepartment;
    private String invigilatorFacultyId;
    private Map<String, Integer> departmentCounts = new HashMap<>();
    private List<ClassroomGridDto> seats = new ArrayList<>();

    public RoomSeatingMatrixDto() {}

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }

    public int getColCount() { return colCount; }
    public void setColCount(int colCount) { this.colCount = colCount; }

    public int getBenchCapacity() { return benchCapacity; }
    public void setBenchCapacity(int benchCapacity) { this.benchCapacity = benchCapacity; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getOccupiedSeats() { return occupiedSeats; }
    public void setOccupiedSeats(int occupiedSeats) { this.occupiedSeats = occupiedSeats; }

    public boolean isCctvAvailable() { return cctvAvailable; }
    public void setCctvAvailable(boolean cctvAvailable) { this.cctvAvailable = cctvAvailable; }

    public String getInvigilatorName() { return invigilatorName; }
    public void setInvigilatorName(String invigilatorName) { this.invigilatorName = invigilatorName; }

    public String getInvigilatorDepartment() { return invigilatorDepartment; }
    public void setInvigilatorDepartment(String invigilatorDepartment) { this.invigilatorDepartment = invigilatorDepartment; }

    public String getInvigilatorFacultyId() { return invigilatorFacultyId; }
    public void setInvigilatorFacultyId(String invigilatorFacultyId) { this.invigilatorFacultyId = invigilatorFacultyId; }

    public Map<String, Integer> getDepartmentCounts() { return departmentCounts; }
    public void setDepartmentCounts(Map<String, Integer> departmentCounts) { this.departmentCounts = departmentCounts; }

    public List<ClassroomGridDto> getSeats() { return seats; }
    public void setSeats(List<ClassroomGridDto> seats) { this.seats = seats; }
}
