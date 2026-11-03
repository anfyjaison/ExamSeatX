package com.examseatx.service;

import com.examseatx.dto.InvigilatorDutyDto;
import com.examseatx.model.Classroom;
import com.examseatx.model.DutyAssignment;
import com.examseatx.model.ExamSession;
import com.examseatx.model.Invigilator;
import com.examseatx.model.SeatAllocation;
import com.examseatx.repository.ClassroomRepository;
import com.examseatx.repository.DutyAssignmentRepository;
import com.examseatx.repository.ExamSessionRepository;
import com.examseatx.repository.InvigilatorRepository;
import com.examseatx.repository.SeatAllocationRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvigilatorService {

    @Autowired
    private InvigilatorRepository invigilatorRepository;

    @Autowired
    private DutyAssignmentRepository dutyAssignmentRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @Autowired
    private SeatAllocationRepository seatAllocationRepository;

    @Transactional
    public List<InvigilatorDutyDto> autoAssignInvigilators(Long examSessionId) {
        ExamSession session = examSessionRepository.findById(examSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Exam session not found: " + examSessionId));

        // Delete old assignments for this session
        dutyAssignmentRepository.deleteByExamSession(session);

        // Find all rooms that have seat allocations in this session
        List<SeatAllocation> allocations = seatAllocationRepository.findByExamSessionOrderByClassroomRoomNumberAscRowNumAscColNumAscBenchSeatNumAsc(session);
        Map<Classroom, List<SeatAllocation>> roomAllocMap = allocations.stream()
                .collect(Collectors.groupingBy(SeatAllocation::getClassroom, LinkedHashMap::new, Collectors.toList()));

        List<Invigilator> availableInvigilators = invigilatorRepository.findByIsAvailableTrueOrderByAssignedDutiesCountAsc();
        List<DutyAssignment> newAssignments = new ArrayList<>();
        Set<Long> assignedInSessionIds = new HashSet<>();

        for (Map.Entry<Classroom, List<SeatAllocation>> entry : roomAllocMap.entrySet()) {
            Classroom room = entry.getKey();
            List<SeatAllocation> roomAlloc = entry.getValue();

            // Distinct departments of students inside this room
            Set<String> roomDepts = roomAlloc.stream()
                    .map(sa -> sa.getStudent().getDepartment().trim().toLowerCase())
                    .collect(Collectors.toSet());

            Set<String> roomBranchCodes = roomAlloc.stream()
                    .map(sa -> sa.getStudent().getBranchCode().trim().toLowerCase())
                    .collect(Collectors.toSet());

            // Department Neutrality Candidate Search:
            // Invigilator department != any exam department in room
            Invigilator chosen = null;

            // Pass 1: Strict neutrality & least loaded & not already assigned this session
            for (Invigilator inv : availableInvigilators) {
                if (assignedInSessionIds.contains(inv.getId())) continue;
                if (inv.getAssignedDutiesCount() >= inv.getMaxDuties()) continue;

                String invDept = inv.getDepartment().trim().toLowerCase();
                boolean matchesRoomDept = roomDepts.stream().anyMatch(d -> d.contains(invDept) || invDept.contains(d));
                boolean matchesBranch = roomBranchCodes.stream().anyMatch(b -> invDept.contains(b));

                if (!matchesRoomDept && !matchesBranch) {
                    chosen = inv;
                    break;
                }
            }

            // Pass 2: Relax duty count limit if strictly necessary, but keep neutrality
            if (chosen == null) {
                for (Invigilator inv : availableInvigilators) {
                    if (assignedInSessionIds.contains(inv.getId())) continue;
                    String invDept = inv.getDepartment().trim().toLowerCase();
                    boolean matchesRoomDept = roomDepts.stream().anyMatch(d -> d.contains(invDept) || invDept.contains(d));
                    if (!matchesRoomDept) {
                        chosen = inv;
                        break;
                    }
                }
            }

            // Pass 3: Fallback any remaining faculty
            if (chosen == null) {
                for (Invigilator inv : availableInvigilators) {
                    if (!assignedInSessionIds.contains(inv.getId())) {
                        chosen = inv;
                        break;
                    }
                }
            }

            if (chosen != null) {
                DutyAssignment da = new DutyAssignment(session, room, chosen, "Chief Invigilator");
                newAssignments.add(da);
                assignedInSessionIds.add(chosen.getId());
                chosen.setAssignedDutiesCount(chosen.getAssignedDutiesCount() + 1);
                invigilatorRepository.save(chosen);
            }
        }

        dutyAssignmentRepository.saveAll(newAssignments);
        return getDutySchedule(session.getId());
    }

    public List<InvigilatorDutyDto> getDutySchedule(Long examSessionId) {
        ExamSession session = examSessionRepository.findById(examSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Exam session not found: " + examSessionId));

        List<DutyAssignment> assignments = dutyAssignmentRepository.findByExamSessionOrderByClassroomRoomNumberAsc(session);
        List<InvigilatorDutyDto> dtos = new ArrayList<>();

        for (DutyAssignment da : assignments) {
            InvigilatorDutyDto dto = new InvigilatorDutyDto();
            dto.setAssignmentId(da.getId());
            dto.setClassroomId(da.getClassroom().getId());
            dto.setRoomNumber(da.getClassroom().getRoomNumber());
            dto.setInvigilatorId(da.getInvigilator().getId());
            dto.setFacultyId(da.getInvigilator().getFacultyId());
            dto.setFacultyName(da.getInvigilator().getName());
            dto.setFacultyDepartment(da.getInvigilator().getDepartment());
            dto.setRole(da.getRole());

            List<SeatAllocation> seats = seatAllocationRepository.findByExamSessionAndClassroomOrderByRowNumAscColNumAscBenchSeatNumAsc(session, da.getClassroom());
            dto.setTotalStudentsInRoom(seats.size());

            List<String> distinctDepts = seats.stream()
                    .map(s -> s.getStudent().getBranchCode() + " (" + s.getStudent().getDepartment() + ")")
                    .distinct()
                    .collect(Collectors.toList());
            dto.setRoomStudentDepartments(distinctDepts);

            // Check department neutrality
            boolean neutral = seats.stream().noneMatch(s -> 
                s.getStudent().getDepartment().equalsIgnoreCase(da.getInvigilator().getDepartment()) ||
                da.getInvigilator().getDepartment().contains(s.getStudent().getBranchCode())
            );
            dto.setDepartmentNeutral(neutral);

            dtos.add(dto);
        }
        return dtos;
    }

    public byte[] exportDutyScheduleExcel(Long examSessionId) throws Exception {
        List<InvigilatorDutyDto> schedule = getDutySchedule(examSessionId);
        ExamSession session = examSessionRepository.findById(examSessionId).orElse(null);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Invigilation Master Schedule");

            // Title styling
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            XSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Header styling
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Row 0: College Header
            XSSFRow r0 = sheet.createRow(0);
            XSSFCell c0 = r0.createCell(0);
            c0.setCellValue("VISWAJYOTHI COLLEGE OF ENGINEERING & TECHNOLOGY");
            c0.setCellStyle(titleStyle);

            // Row 1: Session Info
            XSSFRow r1 = sheet.createRow(1);
            String dateStr = session != null ? session.getSessionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "";
            String sessStr = session != null ? session.getSessionType() : "FN";
            r1.createCell(0).setCellValue("INVIGILATION DUTY MASTER SCHEDULE | Date: " + dateStr + " | Session: " + sessStr);

            // Row 3: Headers
            XSSFRow hr = sheet.createRow(3);
            String[] headers = {"Sl No", "Room No", "Invigilator Name", "Faculty ID", "Faculty Dept", "Exam Depts in Room", "Students Count", "Neutrality Status"};
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = hr.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowIdx = 4;
            int slNo = 1;
            for (InvigilatorDutyDto d : schedule) {
                XSSFRow row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(slNo++);
                row.createCell(1).setCellValue(d.getRoomNumber());
                row.createCell(2).setCellValue(d.getFacultyName());
                row.createCell(3).setCellValue(d.getFacultyId());
                row.createCell(4).setCellValue(d.getFacultyDepartment());
                row.createCell(5).setCellValue(String.join(", ", d.getRoomStudentDepartments()));
                row.createCell(6).setCellValue(d.getTotalStudentsInRoom());
                row.createCell(7).setCellValue(d.isDepartmentNeutral() ? "NEUTRAL (VERIFIED)" : "ATTENTION NEEDED");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] exportDutySchedulePdf(Long examSessionId) throws Exception {
        List<InvigilatorDutyDto> schedule = getDutySchedule(examSessionId);
        ExamSession session = examSessionRepository.findById(examSessionId).orElse(null);

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY);
        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font tdBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);

        Paragraph p1 = new Paragraph("VISWAJYOTHI COLLEGE OF ENGINEERING & TECHNOLOGY", titleFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);

        String dateStr = session != null ? session.getSessionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "";
        String sessStr = session != null ? session.getSessionType() : "FN";
        Paragraph p2 = new Paragraph("UNIVERSITY EXAMINATIONS - INVIGILATION DUTY SCHEDULE\nDate: " + dateStr + " | Session: " + sessStr + "\n\n", subFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.0f, 1.5f, 3.0f, 1.8f, 2.5f, 4.0f, 1.5f});

        String[] headers = {"Sl No", "Room No", "Invigilator Name", "Faculty ID", "Department", "Students Exam Departments", "Students"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, thFont));
            cell.setBackgroundColor(new Color(67, 56, 202)); // Indigo 700
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        int slNo = 1;
        for (InvigilatorDutyDto d : schedule) {
            table.addCell(new Phrase(String.valueOf(slNo++), tdFont));
            table.addCell(new Phrase(d.getRoomNumber(), tdBold));
            table.addCell(new Phrase(d.getFacultyName(), tdBold));
            table.addCell(new Phrase(d.getFacultyId(), tdFont));
            table.addCell(new Phrase(d.getFacultyDepartment(), tdFont));
            table.addCell(new Phrase(String.join(", ", d.getRoomStudentDepartments()), tdFont));
            table.addCell(new Phrase(String.valueOf(d.getTotalStudentsInRoom()), tdFont));
        }

        document.add(table);

        // Signatures at bottom
        Paragraph sig = new Paragraph("\n\n\nChief Superintendent                                                                             Senior Superintendent", tdBold);
        document.add(sig);

        document.close();
        return baos.toByteArray();
    }
}
