package com.examseatx.controller;

import com.examseatx.model.Classroom;
import com.examseatx.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@CrossOrigin(origins = "*")
public class ClassroomController {

    @Autowired
    private ClassroomRepository classroomRepository;

    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms(@RequestParam(required = false) Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return ResponseEntity.ok(classroomRepository.findByActiveTrueOrderByRoomNumberAsc());
        }
        return ResponseEntity.ok(classroomRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id) {
        return classroomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) {
        if (classroom.getBenchCapacity() <= 0) classroom.setBenchCapacity(1);
        classroom.setTotalCapacity(classroom.getRowCount() * classroom.getColCount() * classroom.getBenchCapacity());
        classroom.setActive(true);
        Classroom saved = classroomRepository.save(classroom);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable Long id, @RequestBody Classroom details) {
        return classroomRepository.findById(id).map(room -> {
            room.setRoomNumber(details.getRoomNumber());
            room.setRowCount(details.getRowCount());
            room.setColCount(details.getColCount());
            room.setBenchCapacity(details.getBenchCapacity() > 0 ? details.getBenchCapacity() : 1);
            room.setTotalCapacity(room.getRowCount() * room.getColCount() * room.getBenchCapacity());
            room.setCctvAvailable(details.isCctvAvailable());
            room.setActive(details.isActive());
            return ResponseEntity.ok(classroomRepository.save(room));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Classroom> toggleActiveClassroom(@PathVariable Long id) {
        return classroomRepository.findById(id).map(room -> {
            room.setActive(!room.isActive());
            return ResponseEntity.ok(classroomRepository.save(room));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        if (classroomRepository.existsById(id)) {
            classroomRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
