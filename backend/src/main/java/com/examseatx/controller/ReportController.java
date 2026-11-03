package com.examseatx.controller;

import com.examseatx.model.ExamSession;
import com.examseatx.repository.ExamSessionRepository;
import com.examseatx.service.PdfExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @GetMapping("/notice-board/pdf")
    public ResponseEntity<byte[]> downloadNoticeBoardPdf(@RequestParam(required = false) Long examSessionId) throws Exception {
        Long targetSessionId = getOrCreateSessionId(examSessionId);
        byte[] pdfBytes = pdfExportService.generateNoticeBoardPdf(targetSessionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Notice_Board_Seating_Arrangement.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/attendance/pdf")
    public ResponseEntity<byte[]> downloadAttendancePdf(
            @RequestParam(required = false) Long examSessionId,
            @RequestParam(required = false) Long classroomId) throws Exception {
        Long targetSessionId = getOrCreateSessionId(examSessionId);
        byte[] pdfBytes = pdfExportService.generateAttendanceSheetPdf(targetSessionId, classroomId);

        String filename = classroomId != null ? "Room_" + classroomId + "_Attendance_Sheet.pdf" : "Exam_Attendance_Sheets_All_Rooms.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
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
