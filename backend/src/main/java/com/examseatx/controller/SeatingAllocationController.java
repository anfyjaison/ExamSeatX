package com.examseatx.controller;

import com.examseatx.dto.AllocationRequestDto;
import com.examseatx.dto.AllocationResultDto;
import com.examseatx.dto.ClassroomGridDto;
import com.examseatx.dto.RoomSeatingMatrixDto;
import com.examseatx.model.Classroom;
import com.examseatx.model.ExamSession;
import com.examseatx.model.SeatAllocation;
import com.examseatx.repository.ClassroomRepository;
import com.examseatx.repository.ExamSessionRepository;
import com.examseatx.repository.SeatAllocationRepository;
import com.examseatx.service.CspSeatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/allocations")
@CrossOrigin(origins = "*")
public class SeatingAllocationController {

    @Autowired
    private CspSeatingService cspSeatingService;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @Autowired
    private SeatAllocationRepository seatAllocationRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @PostMapping("/run")
    public ResponseEntity<AllocationResultDto> runAllocation(@RequestBody(required = false) AllocationRequestDto request) {
        if (request == null) {
            request = new AllocationRequestDto();
        }
        AllocationResultDto result = cspSeatingService.allocateSeats(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/matrix")
    public ResponseEntity<List<RoomSeatingMatrixDto>> getSeatingMatrix(@RequestParam(required = false) Long examSessionId) {
        ExamSession session;
        if (examSessionId != null) {
            session = examSessionRepository.findById(examSessionId).orElse(null);
        } else {
            List<ExamSession> active = examSessionRepository.findByActiveTrueOrderBySessionDateDesc();
            session = active.isEmpty() ? null : active.get(0);
        }

        if (session == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<SeatAllocation> allocations = seatAllocationRepository.findByExamSessionOrderByClassroomRoomNumberAscRowNumAscColNumAscBenchSeatNumAsc(session);
        List<Classroom> classrooms = classroomRepository.findByActiveTrueOrderByRoomNumberAsc();

        Map<Classroom, List<SeatAllocation>> byRoom = allocations.stream()
                .collect(Collectors.groupingBy(SeatAllocation::getClassroom, LinkedHashMap::new, Collectors.toList()));

        List<RoomSeatingMatrixDto> matrices = new ArrayList<>();
        for (Classroom room : classrooms) {
            List<SeatAllocation> roomAllocs = byRoom.getOrDefault(room, Collections.emptyList());

            RoomSeatingMatrixDto dto = new RoomSeatingMatrixDto();
            dto.setRoomId(room.getId());
            dto.setRoomNumber(room.getRoomNumber());
            dto.setRowCount(room.getRowCount());
            dto.setColCount(room.getColCount());
            dto.setBenchCapacity(room.getBenchCapacity());
            dto.setTotalCapacity(room.getTotalCapacity());
            dto.setOccupiedSeats(roomAllocs.size());
            dto.setCctvAvailable(room.isCctvAvailable());

            Map<String, Integer> deptCounts = new HashMap<>();
            Map<String, SeatAllocation> seatMap = new HashMap<>();
            for (SeatAllocation sa : roomAllocs) {
                seatMap.put(sa.getRowNum() + "-" + sa.getColNum() + "-" + sa.getBenchSeatNum(), sa);
                deptCounts.put(sa.getStudent().getBranchCode(), deptCounts.getOrDefault(sa.getStudent().getBranchCode(), 0) + 1);
            }
            dto.setDepartmentCounts(deptCounts);

            List<ClassroomGridDto> gridCells = new ArrayList<>();
            for (int r = 1; r <= room.getRowCount(); r++) {
                for (int c = 1; c <= room.getColCount(); c++) {
                    for (int b = 1; b <= room.getBenchCapacity(); b++) {
                        ClassroomGridDto cell = new ClassroomGridDto();
                        cell.setRowNum(r);
                        cell.setColNum(c);
                        cell.setBenchSeatNum(b);
                        cell.setSeatLabel("R" + r + "-C" + c + (room.getBenchCapacity() > 1 ? "-S" + b : ""));

                        SeatAllocation sa = seatMap.get(r + "-" + c + "-" + b);
                        if (sa != null) {
                            cell.setStudentId(sa.getStudent().getId());
                            cell.setRegisterNumber(sa.getStudent().getRegisterNumber());
                            cell.setStudentName(sa.getStudent().getStudentName());
                            cell.setDepartment(sa.getStudent().getDepartment());
                            cell.setBranchCode(sa.getStudent().getBranchCode());
                            cell.setSubjectCode(sa.getStudent().getSubjectCode());
                            cell.setSubjectName(sa.getStudent().getSubjectName());
                            cell.setExamType(sa.getStudent().getExamType());
                        }
                        gridCells.add(cell);
                    }
                }
            }
            dto.setSeats(gridCells);
            matrices.add(dto);
        }

        return ResponseEntity.ok(matrices);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ExamSession>> getExamSessions() {
        return ResponseEntity.ok(examSessionRepository.findByActiveTrueOrderBySessionDateDesc());
    }

    @PostMapping("/sessions")
    public ResponseEntity<ExamSession> createExamSession(@RequestBody ExamSession session) {
        if (session.getSessionDate() == null) session.setSessionDate(LocalDate.now());
        if (session.getSessionType() == null) session.setSessionType("FN");
        return ResponseEntity.ok(examSessionRepository.save(session));
    }
}
