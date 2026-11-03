package com.examseatx.service;

import com.examseatx.dto.AllocationRequestDto;
import com.examseatx.dto.AllocationResultDto;
import com.examseatx.dto.ClassroomGridDto;
import com.examseatx.dto.RoomSeatingMatrixDto;
import com.examseatx.model.Classroom;
import com.examseatx.model.ExamSession;
import com.examseatx.model.SeatAllocation;
import com.examseatx.model.Student;
import com.examseatx.repository.ClassroomRepository;
import com.examseatx.repository.ExamSessionRepository;
import com.examseatx.repository.SeatAllocationRepository;
import com.examseatx.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CspSeatingService {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SeatAllocationRepository seatAllocationRepository;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @Autowired
    private InvigilatorService invigilatorService;

    // Helper representation of a physical seat
    public static class SeatPos {
        public final Classroom classroom;
        public final int row;
        public final int col;
        public final int benchSeat; // 1 or 2
        public final int physicalX; // Horizontal coordinate
        public final int physicalY; // Vertical coordinate
        public Student assignedStudent = null;

        public SeatPos(Classroom classroom, int row, int col, int benchSeat) {
            this.classroom = classroom;
            this.row = row;
            this.col = col;
            this.benchSeat = benchSeat;
            this.physicalX = (col - 1) * classroom.getBenchCapacity() + benchSeat;
            this.physicalY = row;
        }

        public String getKey() {
            return classroom.getRoomNumber() + "_" + row + "_" + col + "_" + benchSeat;
        }
    }

    @Transactional
    public AllocationResultDto allocateSeats(AllocationRequestDto request) {
        long startTime = System.currentTimeMillis();
        AllocationResultDto result = new AllocationResultDto();

        ExamSession session;
        if (request.getExamSessionId() != null) {
            session = examSessionRepository.findById(request.getExamSessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Exam session not found: " + request.getExamSessionId()));
        } else {
            List<ExamSession> active = examSessionRepository.findByActiveTrueOrderBySessionDateDesc();
            if (active.isEmpty()) {
                throw new IllegalStateException("No active exam session found.");
            }
            session = active.get(0);
        }

        // Get classrooms
        List<Classroom> classrooms;
        if (request.getClassroomIds() != null && !request.getClassroomIds().isEmpty()) {
            classrooms = classroomRepository.findAllById(request.getClassroomIds());
        } else {
            classrooms = classroomRepository.findByActiveTrueOrderByRoomNumberAsc();
        }

        // Get students
        List<Student> students;
        if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            students = studentRepository.findAllById(request.getStudentIds());
        } else {
            students = studentRepository.findAll();
        }

        if (classrooms.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("No active classrooms available for allocation.");
            return result;
        }

        if (students.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("No students available for seating allocation.");
            return result;
        }

        // Clear existing allocations for this session
        seatAllocationRepository.deleteByExamSession(session);

        // Compute total capacity
        int totalCapacity = classrooms.stream().mapToInt(Classroom::getTotalCapacity).sum();
        if (totalCapacity < students.size()) {
            result.getWarnings().add("Total classroom capacity (" + totalCapacity + ") is less than student count (" 
                    + students.size() + "). " + (students.size() - totalCapacity) + " students will remain unallocated.");
        }

        // Prepare physical seats across all requested classrooms
        Map<String, List<SeatPos>> roomSeatsMap = new LinkedHashMap<>();
        for (Classroom room : classrooms) {
            List<SeatPos> roomSeats = new ArrayList<>();
            for (int r = 1; r <= room.getRowCount(); r++) {
                for (int c = 1; c <= room.getColCount(); c++) {
                    for (int b = 1; b <= room.getBenchCapacity(); b++) {
                        roomSeats.add(new SeatPos(room, r, c, b));
                    }
                }
            }
            roomSeatsMap.put(room.getRoomNumber(), roomSeats);
        }

        // Separate Regular and Supplementary students and organize by Department
        List<Student> regularStudents = students.stream()
                .filter(s -> !"Supplementary".equalsIgnoreCase(s.getExamType()))
                .collect(Collectors.toList());
        List<Student> supplementaryStudents = students.stream()
                .filter(s -> "Supplementary".equalsIgnoreCase(s.getExamType()))
                .collect(Collectors.toList());

        // Interleave students to maximize diversity in queue (Round-Robin by Dept)
        List<Student> prioritizedStudents = buildInterleavedStudentQueue(regularStudents, supplementaryStudents);

        // Execute CSP Allocation with Greedy placement + Intelligent Backtracking
        Map<String, List<SeatPos>> allocatedRooms = solveCspSeating(roomSeatsMap, prioritizedStudents, request.isEnforceDiagonal());

        // Persist Allocations to DB
        List<SeatAllocation> entitiesToSave = new ArrayList<>();
        int allocatedCount = 0;
        int violationCount = 0;

        for (Map.Entry<String, List<SeatPos>> entry : allocatedRooms.entrySet()) {
            for (SeatPos seat : entry.getValue()) {
                if (seat.assignedStudent != null) {
                    SeatAllocation sa = new SeatAllocation(session, seat.classroom, seat.row, seat.col, seat.benchSeat, seat.assignedStudent);
                    entitiesToSave.add(sa);
                    allocatedCount++;
                }
            }
        }
        seatAllocationRepository.saveAll(entitiesToSave);

        // Auto-assign invigilators with Department Neutrality if requested
        if (request.isAutoAssignInvigilators()) {
            try {
                invigilatorService.autoAssignInvigilators(session.getId());
            } catch (Exception e) {
                result.getWarnings().add("Invigilator auto-assignment notice: " + e.getMessage());
            }
        }

        // Build response DTOs
        List<RoomSeatingMatrixDto> roomMatrices = buildRoomMatrices(allocatedRooms, session);

        // Calculate violation count
        for (RoomSeatingMatrixDto rm : roomMatrices) {
            for (ClassroomGridDto seatDto : rm.getSeats()) {
                if (seatDto.isConflict()) {
                    violationCount++;
                }
            }
        }

        result.setSuccess(true);
        result.setMessage("Seating allocation completed successfully using CSP non-adjacency engine.");
        result.setTotalStudentsAllocated(allocatedCount);
        result.setUnallocatedStudentsCount(Math.max(0, students.size() - allocatedCount));
        result.setTotalRoomsUsed((int) roomMatrices.stream().filter(m -> m.getOccupiedSeats() > 0).count());
        result.setConstraintViolations(violationCount);
        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        result.setRoomMatrices(roomMatrices);

        return result;
    }

    /**
     * Round-robin interleaves departments while placing supplementary students strategically
     * to prevent department clumps.
     */
    private List<Student> buildInterleavedStudentQueue(List<Student> regular, List<Student> supplementary) {
        // Group regular by department
        Map<String, Queue<Student>> regByDept = new LinkedHashMap<>();
        for (Student s : regular) {
            regByDept.computeIfAbsent(s.getDepartment(), k -> new LinkedList<>()).add(s);
        }

        // Group supplementary by department
        Map<String, Queue<Student>> suppByDept = new LinkedHashMap<>();
        for (Student s : supplementary) {
            suppByDept.computeIfAbsent(s.getDepartment(), k -> new LinkedList<>()).add(s);
        }

        List<Student> queue = new ArrayList<>();
        List<String> depts = new ArrayList<>(regByDept.keySet());
        for (String d : suppByDept.keySet()) {
            if (!depts.contains(d)) depts.add(d);
        }

        boolean hasMore = true;
        while (hasMore) {
            hasMore = false;
            for (String dept : depts) {
                // First pull supplementary if available (acts as great variance buffer)
                Queue<Student> suppQ = suppByDept.get(dept);
                if (suppQ != null && !suppQ.isEmpty()) {
                    queue.add(suppQ.poll());
                    hasMore = true;
                }
                // Then regular
                Queue<Student> regQ = regByDept.get(dept);
                if (regQ != null && !regQ.isEmpty()) {
                    queue.add(regQ.poll());
                    hasMore = true;
                }
            }
        }
        return queue;
    }

    /**
     * Core CSP Solver: Combines greedy heuristic ordering with intelligent backtracking.
     */
    private Map<String, List<SeatPos>> solveCspSeating(Map<String, List<SeatPos>> roomSeatsMap, 
                                                       List<Student> students, 
                                                       boolean enforceDiagonal) {
        // Flatten seats into room-aware ordered list
        List<SeatPos> allSeats = new ArrayList<>();
        for (List<SeatPos> roomSeats : roomSeatsMap.values()) {
            // Sort seats in alternating serpentine order (Row 1 L->R, Row 2 R->L) to scatter adjacent placements
            roomSeats.sort((a, b) -> {
                if (a.row != b.row) return Integer.compare(a.row, b.row);
                int colComp = (a.row % 2 == 1) ? Integer.compare(a.col, b.col) : Integer.compare(b.col, a.col);
                if (colComp != 0) return colComp;
                return Integer.compare(a.benchSeat, b.benchSeat);
            });
            allSeats.addAll(roomSeats);
        }

        List<Student> unassignedStudents = new LinkedList<>(students);

        for (SeatPos seat : allSeats) {
            if (unassignedStudents.isEmpty()) break;

            // Find best candidate student satisfying non-adjacency
            Student chosenStudent = null;
            int chosenIndex = -1;

            for (int i = 0; i < unassignedStudents.size(); i++) {
                Student candidate = unassignedStudents.get(i);
                if (isSeatValidForStudent(seat, candidate, roomSeatsMap.get(seat.classroom.getRoomNumber()), enforceDiagonal)) {
                    chosenStudent = candidate;
                    chosenIndex = i;
                    break;
                }
            }

            if (chosenStudent != null) {
                seat.assignedStudent = chosenStudent;
                unassignedStudents.remove(chosenIndex);
            } else {
                // Bottleneck resolution: Intelligent Backtracking
                // Try swapping with a nearby previous seat in the same room
                boolean resolved = resolveBottleneck(seat, unassignedStudents, roomSeatsMap.get(seat.classroom.getRoomNumber()), enforceDiagonal);
                if (!resolved) {
                    // If hard constraint cannot be satisfied without leaving seat empty,
                    // pick student with minimal conflict (or leave seat empty if strict)
                    // We pick least conflicting candidate so room capacity is utilized
                    Student fallback = findLeastConflictingStudent(seat, unassignedStudents, roomSeatsMap.get(seat.classroom.getRoomNumber()));
                    if (fallback != null) {
                        seat.assignedStudent = fallback;
                        unassignedStudents.remove(fallback);
                    }
                }
            }
        }

        return roomSeatsMap;
    }

    /**
     * Verifies 8-way spatial non-adjacency constraint:
     * No horizontally, vertically, or diagonally adjacent seat shares the same Department or Subject Code.
     */
    private boolean isSeatValidForStudent(SeatPos targetSeat, Student candidate, List<SeatPos> roomSeats, boolean checkDiagonal) {
        for (SeatPos other : roomSeats) {
            if (other.assignedStudent == null || other == targetSeat) continue;

            int dx = Math.abs(other.physicalX - targetSeat.physicalX);
            int dy = Math.abs(other.physicalY - targetSeat.physicalY);

            // Directly adjacent horizontally (dx == 1, dy == 0)
            if (dx == 1 && dy == 0) {
                if (hasConflict(other.assignedStudent, candidate)) {
                    return false;
                }
            }

            // Directly adjacent vertically (dx == 0, dy == 1)
            if (dx == 0 && dy == 1) {
                if (hasConflict(other.assignedStudent, candidate)) {
                    return false;
                }
            }

            // Diagonally adjacent (dx == 1, dy == 1)
            if (checkDiagonal && dx == 1 && dy == 1) {
                if (hasConflict(other.assignedStudent, candidate)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasConflict(Student a, Student b) {
        if (a == null || b == null) return false;
        if (a.getDepartment().trim().equalsIgnoreCase(b.getDepartment().trim())) return true;
        if (a.getBranchCode().trim().equalsIgnoreCase(b.getBranchCode().trim())) return true;
        if (a.getSubjectCode().trim().equalsIgnoreCase(b.getSubjectCode().trim())) return true;
        return false;
    }

    /**
     * Backtracking resolver: finds an earlier assigned seat in the room that can take the current candidate,
     * freeing up an existing student whose department works at this target seat.
     */
    private boolean resolveBottleneck(SeatPos currentSeat, List<Student> unassigned, List<SeatPos> roomSeats, boolean checkDiagonal) {
        for (int i = 0; i < unassigned.size(); i++) {
            Student cand = unassigned.get(i);
            for (SeatPos prev : roomSeats) {
                if (prev.assignedStudent == null || prev == currentSeat) continue;
                Student prevStudent = prev.assignedStudent;

                prev.assignedStudent = null;
                boolean candFitsPrev = isSeatValidForStudent(prev, cand, roomSeats, checkDiagonal);
                prev.assignedStudent = prevStudent;

                if (candFitsPrev && isSeatValidForStudent(currentSeat, prevStudent, roomSeats, checkDiagonal)) {
                    prev.assignedStudent = cand;
                    currentSeat.assignedStudent = prevStudent;
                    unassigned.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    private Student findLeastConflictingStudent(SeatPos seat, List<Student> unassigned, List<SeatPos> roomSeats) {
        if (unassigned.isEmpty()) return null;
        Student best = null;
        int minConflicts = Integer.MAX_VALUE;

        for (Student s : unassigned) {
            int conflicts = 0;
            for (SeatPos other : roomSeats) {
                if (other.assignedStudent == null || other == seat) continue;
                int dx = Math.abs(other.physicalX - seat.physicalX);
                int dy = Math.abs(other.physicalY - seat.physicalY);
                if (dx <= 1 && dy <= 1) {
                    if (hasConflict(other.assignedStudent, s)) conflicts++;
                }
            }
            if (conflicts < minConflicts) {
                minConflicts = conflicts;
                best = s;
                if (conflicts == 0) break;
            }
        }
        return best;
    }

    private List<RoomSeatingMatrixDto> buildRoomMatrices(Map<String, List<SeatPos>> allocatedRooms, ExamSession session) {
        List<RoomSeatingMatrixDto> matrices = new ArrayList<>();

        for (Map.Entry<String, List<SeatPos>> entry : allocatedRooms.entrySet()) {
            List<SeatPos> seats = entry.getValue();
            if (seats.isEmpty()) continue;
            Classroom room = seats.get(0).classroom;

            RoomSeatingMatrixDto matrix = new RoomSeatingMatrixDto();
            matrix.setRoomId(room.getId());
            matrix.setRoomNumber(room.getRoomNumber());
            matrix.setRowCount(room.getRowCount());
            matrix.setColCount(room.getColCount());
            matrix.setBenchCapacity(room.getBenchCapacity());
            matrix.setTotalCapacity(room.getTotalCapacity());
            matrix.setCctvAvailable(room.isCctvAvailable());

            int occupied = 0;
            Map<String, Integer> deptCounts = new HashMap<>();
            List<ClassroomGridDto> seatDtos = new ArrayList<>();

            for (SeatPos sp : seats) {
                ClassroomGridDto dto = new ClassroomGridDto();
                dto.setRowNum(sp.row);
                dto.setColNum(sp.col);
                dto.setBenchSeatNum(sp.benchSeat);
                dto.setSeatLabel("R" + sp.row + "-C" + sp.col + (room.getBenchCapacity() > 1 ? "-S" + sp.benchSeat : ""));

                if (sp.assignedStudent != null) {
                    occupied++;
                    Student st = sp.assignedStudent;
                    dto.setStudentId(st.getId());
                    dto.setRegisterNumber(st.getRegisterNumber());
                    dto.setStudentName(st.getStudentName());
                    dto.setDepartment(st.getDepartment());
                    dto.setBranchCode(st.getBranchCode());
                    dto.setSubjectCode(st.getSubjectCode());
                    dto.setSubjectName(st.getSubjectName());
                    dto.setExamType(st.getExamType());

                    deptCounts.put(st.getBranchCode(), deptCounts.getOrDefault(st.getBranchCode(), 0) + 1);

                    // Check for conflicts in current matrix using physical coordinates
                    List<String> conflictReasons = new ArrayList<>();
                    for (SeatPos neighbor : seats) {
                        if (neighbor == sp || neighbor.assignedStudent == null) continue;
                        int dx = Math.abs(neighbor.physicalX - sp.physicalX);
                        int dy = Math.abs(neighbor.physicalY - sp.physicalY);
                        if (dx <= 1 && dy <= 1) {
                            if (neighbor.assignedStudent.getDepartment().equalsIgnoreCase(st.getDepartment()) ||
                                neighbor.assignedStudent.getBranchCode().equalsIgnoreCase(st.getBranchCode())) {
                                conflictReasons.add("Adjacent same Dept (" + st.getBranchCode() + ") with " + neighbor.assignedStudent.getRegisterNumber());
                            }
                            if (neighbor.assignedStudent.getSubjectCode().equalsIgnoreCase(st.getSubjectCode())) {
                                conflictReasons.add("Adjacent same Subject (" + st.getSubjectCode() + ") with " + neighbor.assignedStudent.getRegisterNumber());
                            }
                        }
                    }
                    if (!conflictReasons.isEmpty()) {
                        dto.setConflict(true);
                        dto.setConflictReason(String.join("; ", conflictReasons));
                    }
                }
                seatDtos.add(dto);
            }

            matrix.setOccupiedSeats(occupied);
            matrix.setDepartmentCounts(deptCounts);
            matrix.setSeats(seatDtos);
            matrices.add(matrix);
        }

        return matrices;
    }
}
