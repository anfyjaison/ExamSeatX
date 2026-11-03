package com.examseatx.controller;

import com.examseatx.dto.InvigilatorDutyDto;
import com.examseatx.model.ExamSession;
import com.examseatx.model.Invigilator;
import com.examseatx.repository.ExamSessionRepository;
import com.examseatx.repository.InvigilatorRepository;
import com.examseatx.service.InvigilatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invigilators")
@CrossOrigin(origins = "*")
public class InvigilatorController {

    @Autowired
    private InvigilatorRepository invigilatorRepository;

    @Autowired
    private InvigilatorService invigilatorService;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @GetMapping
    public ResponseEntity<List<Invigilator>> getAllInvigilators() {
        return ResponseEntity.ok(invigilatorRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Invigilator> createInvigilator(@RequestBody Invigilator invigilator) {
        if (invigilator.getMaxDuties() <= 0) invigilator.setMaxDuties(5);
        return ResponseEntity.ok(invigilatorRepository.save(invigilator));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invigilator> updateInvigilator(@PathVariable Long id, @RequestBody Invigilator details) {
        return invigilatorRepository.findById(id).map(inv -> {
            inv.setName(details.getName());
            inv.setFacultyId(details.getFacultyId());
            inv.setDepartment(details.getDepartment());
            inv.setEmail(details.getEmail());
            inv.setPhone(details.getPhone());
            inv.setMaxDuties(details.getMaxDuties());
            inv.setAvailable(details.isAvailable());
            return ResponseEntity.ok(invigilatorRepository.save(inv));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvigilator(@PathVariable Long id) {
        if (invigilatorRepository.existsById(id)) {
            invigilatorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/auto-assign")
    public ResponseEntity<List<InvigilatorDutyDto>> autoAssign(@RequestParam(required = false) Long examSessionId) {
        Long targetSessionId = getOrCreateSessionId(examSessionId);
        List<InvigilatorDutyDto> schedule = invigilatorService.autoAssignInvigilators(targetSessionId);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/duties")
    public ResponseEntity<List<InvigilatorDutyDto>> getDuties(@RequestParam(required = false) Long examSessionId) {
        Long targetSessionId = getOrCreateSessionId(examSessionId);
        List<InvigilatorDutyDto> duties = invigilatorService.getDutySchedule(targetSessionId);
        return ResponseEntity.ok(duties);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportDutiesExcel(@RequestParam(required = false) Long examSessionId) throws Exception {
        Long targetSessionId = getOrCreateSessionId(examSessionId);
        byte[] excelBytes = invigilatorService.exportDutyScheduleExcel(targetSessionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Invigilation_Duty_Master_Schedule.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportDutiesPdf(@RequestParam(required = false) Long examSessionId) throws Exception {
        Long targetSessionId = getOrCreateSessionId(examSessionId);
        byte[] pdfBytes = invigilatorService.exportDutySchedulePdf(targetSessionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Invigilation_Duty_Schedule.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    private Long getOrCreateSessionId(Long examSessionId) {
        if (examSessionId != null) return examSessionId;
        List<ExamSession> active = examSessionRepository.findByActiveTrueOrderBySessionDateDesc();
        if (!active.isEmpty()) return active.get(0).getId();
        throw new IllegalStateException("No active exam session found.");
    }
}
