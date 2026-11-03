package com.examseatx.dto;

import java.util.ArrayList;
import java.util.List;

public class AllocationResultDto {
    private boolean success;
    private String message;
    private int totalStudentsAllocated;
    private int unallocatedStudentsCount;
    private int totalRoomsUsed;
    private long executionTimeMs;
    private int constraintViolations;
    private List<String> warnings = new ArrayList<>();
    private List<RoomSeatingMatrixDto> roomMatrices = new ArrayList<>();

    public AllocationResultDto() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getTotalStudentsAllocated() { return totalStudentsAllocated; }
    public void setTotalStudentsAllocated(int totalStudentsAllocated) { this.totalStudentsAllocated = totalStudentsAllocated; }

    public int getUnallocatedStudentsCount() { return unallocatedStudentsCount; }
    public void setUnallocatedStudentsCount(int unallocatedStudentsCount) { this.unallocatedStudentsCount = unallocatedStudentsCount; }

    public int getTotalRoomsUsed() { return totalRoomsUsed; }
    public void setTotalRoomsUsed(int totalRoomsUsed) { this.totalRoomsUsed = totalRoomsUsed; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public int getConstraintViolations() { return constraintViolations; }
    public void setConstraintViolations(int constraintViolations) { this.constraintViolations = constraintViolations; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public List<RoomSeatingMatrixDto> getRoomMatrices() { return roomMatrices; }
    public void setRoomMatrices(List<RoomSeatingMatrixDto> roomMatrices) { this.roomMatrices = roomMatrices; }
}
