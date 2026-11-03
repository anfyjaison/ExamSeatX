package com.examseatx.controller;

import com.examseatx.model.Student;
import com.examseatx.repository.StudentRepository;
import com.examseatx.service.ExcelParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExcelParserService excelParserService;

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String examType) {
        if (examType != null && !examType.isEmpty()) {
            return ResponseEntity.ok(studentRepository.findByExamTypeIgnoreCase(examType));
        }
        if (department != null && !department.isEmpty()) {
            return ResponseEntity.ok(studentRepository.findByDepartmentOrderByRegisterNumberAsc(department));
        }
        return ResponseEntity.ok(studentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        if (student.getBranchCode() == null || student.getBranchCode().trim().isEmpty()) {
            student.setBranchCode(excelParserService.inferBranchCode(student.getRegisterNumber()));
        }
        if (student.getDepartment() == null || student.getDepartment().trim().isEmpty()) {
            student.setDepartment(excelParserService.inferDepartment(student.getBranchCode()));
        }
        Student saved = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student details) {
        return studentRepository.findById(id).map(s -> {
            s.setRegisterNumber(details.getRegisterNumber());
            s.setStudentName(details.getStudentName());
            s.setDepartment(details.getDepartment());
            s.setBranchCode(details.getBranchCode());
            s.setSemester(details.getSemester());
            s.setSubjectCode(details.getSubjectCode());
            s.setSubjectName(details.getSubjectName());
            s.setExamType(details.getExamType());
            s.setSlot(details.getSlot());
            return ResponseEntity.ok(studentRepository.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearAllStudents() {
        studentRepository.deleteAll();
        Map<String, String> res = new HashMap<>();
        res.put("message", "All student records removed successfully.");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadStudentRoster(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Uploaded file is empty.");
                return ResponseEntity.badRequest().body(response);
            }

            List<Student> parsedStudents = excelParserService.parseStudentRoster(file);
            int newCount = 0;
            int updatedCount = 0;

            for (Student s : parsedStudents) {
                var existing = studentRepository.findByRegisterNumber(s.getRegisterNumber());
                if (existing.isPresent()) {
                    Student ex = existing.get();
                    ex.setStudentName(s.getStudentName());
                    ex.setDepartment(s.getDepartment());
                    ex.setBranchCode(s.getBranchCode());
                    ex.setSemester(s.getSemester());
                    ex.setSubjectCode(s.getSubjectCode());
                    ex.setSubjectName(s.getSubjectName());
                    ex.setExamType(s.getExamType());
                    ex.setSlot(s.getSlot());
                    studentRepository.save(ex);
                    updatedCount++;
                } else {
                    studentRepository.save(s);
                    newCount++;
                }
            }

            response.put("success", true);
            response.put("message", "Successfully imported student roster.");
            response.put("totalParsed", parsedStudents.size());
            response.put("inserted", newCount);
            response.put("updated", updatedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to parse roster file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
