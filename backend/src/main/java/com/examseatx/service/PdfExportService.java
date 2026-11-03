package com.examseatx.service;

import com.examseatx.model.Classroom;
import com.examseatx.model.ExamSession;
import com.examseatx.model.SeatAllocation;
import com.examseatx.model.Student;
import com.examseatx.repository.ClassroomRepository;
import com.examseatx.repository.DutyAssignmentRepository;
import com.examseatx.repository.ExamSessionRepository;
import com.examseatx.repository.SeatAllocationRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfExportService {

    @Autowired
    private SeatAllocationRepository seatAllocationRepository;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private DutyAssignmentRepository dutyAssignmentRepository;

    /**
     * Generates Notice Board Seating Arrangement PDF matching the university exam format
     * with grouped ranges / register numbers per classroom and branch.
     */
    public byte[] generateNoticeBoardPdf(Long examSessionId) throws Exception {
        ExamSession session = examSessionRepository.findById(examSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Exam session not found: " + examSessionId));

        List<SeatAllocation> allocations = seatAllocationRepository.findByExamSessionOrderByClassroomRoomNumberAscRowNumAscColNumAscBenchSeatNumAsc(session);

        Document document = new Document(PageSize.A4, 25, 25, 25, 25);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        Font collegeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font examTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        Font mainSeatingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
        Font metaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font tdBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);

        // Header
        Paragraph p1 = new Paragraph("VISWAJYOTHI COLLEGE OF ENGINEERING & TECHNOLOGY", collegeFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);

        Paragraph p2 = new Paragraph(session.getExamTitle() + " " + session.getSemester(), examTitleFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);

        Paragraph p3 = new Paragraph("MAIN SEATING ARRANGEMENT", mainSeatingFont);
        p3.setAlignment(Element.ALIGN_CENTER);
        document.add(p3);
        document.add(new Paragraph(" "));

        // Date and Session meta line
        String dateStr = session.getSessionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        PdfPCell cDate = new PdfPCell(new Phrase("Date : " + dateStr, metaFont));
        cDate.setBorder(Rectangle.NO_BORDER);
        PdfPCell cSess = new PdfPCell(new Phrase("Session : " + session.getSessionType(), metaFont));
        cSess.setBorder(Rectangle.NO_BORDER);
        cSess.setHorizontalAlignment(Element.ALIGN_RIGHT);
        metaTable.addCell(cDate);
        metaTable.addCell(cSess);
        document.add(metaTable);
        document.add(new Paragraph(" "));

        // Group allocations by Classroom, then by Branch/Dept
        Map<Classroom, Map<String, List<Student>>> roomBranchMap = new LinkedHashMap<>();
        for (SeatAllocation sa : allocations) {
            Classroom rm = sa.getClassroom();
            Student st = sa.getStudent();
            String branchKey = st.getSemester() + " - " + st.getBranchCode();
            roomBranchMap
                    .computeIfAbsent(rm, k -> new LinkedHashMap<>())
                    .computeIfAbsent(branchKey, k -> new ArrayList<>())
                    .add(st);
        }

        PdfPTable mainTable = new PdfPTable(3);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{2.0f, 1.8f, 7.5f});

        // Table Header
        PdfPCell h1 = new PdfPCell(new Phrase("Class Room", thFont));
        h1.setBackgroundColor(new Color(240, 240, 240));
        h1.setPadding(4);
        PdfPCell h2 = new PdfPCell(new Phrase("Branch", thFont));
        h2.setBackgroundColor(new Color(240, 240, 240));
        h2.setPadding(4);
        PdfPCell h3 = new PdfPCell(new Phrase("Register Number", thFont));
        h3.setBackgroundColor(new Color(240, 240, 240));
        h3.setPadding(4);
        mainTable.addCell(h1);
        mainTable.addCell(h2);
        mainTable.addCell(h3);

        for (Map.Entry<Classroom, Map<String, List<Student>>> roomEntry : roomBranchMap.entrySet()) {
            Classroom room = roomEntry.getKey();
            Map<String, List<Student>> branchMap = roomEntry.getValue();
            int branchesCount = branchMap.size();

            boolean isFirstBranch = true;
            for (Map.Entry<String, List<Student>> branchEntry : branchMap.entrySet()) {
                String branch = branchEntry.getKey();
                List<Student> branchStudents = branchEntry.getValue();

                // Sort students by Register Number
                branchStudents.sort(Comparator.comparing(Student::getRegisterNumber));

                // Classroom Cell (spanned or first row)
                if (isFirstBranch) {
                    PdfPCell roomCell = new PdfPCell(new Phrase(room.getRoomNumber(), tdBold));
                    roomCell.setRowspan(branchesCount);
                    roomCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    roomCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    roomCell.setPadding(5);
                    mainTable.addCell(roomCell);
                    isFirstBranch = false;
                }

                // Branch Cell
                PdfPCell branchCell = new PdfPCell(new Phrase(branch, tdBold));
                branchCell.setPadding(4);
                mainTable.addCell(branchCell);

                // Formatted Register Number List / Condensed Ranges
                String formattedRegs = formatRegisterNumbersCondensed(branchStudents);
                PdfPCell regCell = new PdfPCell(new Phrase(formattedRegs, tdFont));
                regCell.setPadding(4);
                mainTable.addCell(regCell);
            }
        }

        document.add(mainTable);

        // Footer
        Paragraph chiefSupdt = new Paragraph("\n\n\nChief Supdt.", tdBold);
        chiefSupdt.setAlignment(Element.ALIGN_RIGHT);
        document.add(chiefSupdt);

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generates Room Attendance & Signature Sheet PDF for a specific room or all rooms in session.
     */
    public byte[] generateAttendanceSheetPdf(Long examSessionId, Long classroomId) throws Exception {
        ExamSession session = examSessionRepository.findById(examSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Exam session not found: " + examSessionId));

        List<Classroom> classrooms;
        if (classroomId != null) {
            Classroom r = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId));
            classrooms = Collections.singletonList(r);
        } else {
            classrooms = classroomRepository.findByActiveTrueOrderByRoomNumberAsc();
        }

        Document document = new Document(PageSize.A4, 25, 25, 25, 25);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        Font h1Font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font h2Font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY);
        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
        Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font tdBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);

        boolean firstPage = true;

        for (Classroom room : classrooms) {
            List<SeatAllocation> allocations = seatAllocationRepository.findByExamSessionAndClassroomOrderByRowNumAscColNumAscBenchSeatNumAsc(session, room);
            if (allocations.isEmpty()) continue;

            if (!firstPage) {
                document.newPage();
            }
            firstPage = false;

            // Header
            Paragraph title = new Paragraph("VISWAJYOTHI COLLEGE OF ENGINEERING & TECHNOLOGY", h1Font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("ROOM ATTENDANCE & VERIFICATION STATEMENT\n" + session.getExamTitle() + " (" + session.getSemester() + ")", h2Font);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);
            document.add(new Paragraph(" "));

            // Room & Invigilator Info Box
            String invigilatorText = dutyAssignmentRepository.findByExamSessionAndClassroom(session, room)
                    .map(da -> da.getInvigilator().getName() + " (" + da.getInvigilator().getFacultyId() + ") - " + da.getInvigilator().getDepartment())
                    .orElse("To be assigned");

            PdfPTable infoTable = new PdfPTable(3);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{3.0f, 3.0f, 4.0f});

            infoTable.addCell(createBorderedCell("Class Room: " + room.getRoomNumber(), tdBold));
            infoTable.addCell(createBorderedCell("Date: " + session.getSessionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " (" + session.getSessionType() + ")", tdBold));
            infoTable.addCell(createBorderedCell("Invigilator: " + invigilatorText, tdFont));

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // Attendance Table
            PdfPTable attTable = new PdfPTable(8);
            attTable.setWidthPercentage(100);
            attTable.setWidths(new float[]{0.8f, 1.4f, 2.3f, 3.0f, 1.3f, 1.2f, 1.8f, 1.8f});

            String[] headers = {"Sl", "Seat", "Register No", "Student Name", "Branch", "Course", "Booklet No", "Signature"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, thFont));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setPadding(4);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                attTable.addCell(cell);
            }

            int sl = 1;
            for (SeatAllocation sa : allocations) {
                Student st = sa.getStudent();
                attTable.addCell(createAttCell(String.valueOf(sl++), tdFont, Element.ALIGN_CENTER));
                attTable.addCell(createAttCell("R" + sa.getRowNum() + "-C" + sa.getColNum(), tdBold, Element.ALIGN_CENTER));
                attTable.addCell(createAttCell(st.getRegisterNumber(), tdBold, Element.ALIGN_LEFT));
                attTable.addCell(createAttCell(st.getStudentName(), tdFont, Element.ALIGN_LEFT));
                attTable.addCell(createAttCell(st.getBranchCode(), tdFont, Element.ALIGN_CENTER));
                attTable.addCell(createAttCell(st.getSubjectCode(), tdFont, Element.ALIGN_CENTER));
                attTable.addCell(createAttCell("", tdFont, Element.ALIGN_CENTER)); // Blank for booklet
                attTable.addCell(createAttCell("", tdFont, Element.ALIGN_CENTER)); // Blank for signature
            }
            document.add(attTable);

            // Summary Verification Block
            document.add(new Paragraph("\n"));
            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{2.5f, 2.5f, 2.5f, 2.5f});
            summaryTable.addCell(createBorderedCell("Total Allotted: " + allocations.size(), tdBold));
            summaryTable.addCell(createBorderedCell("Present Count: ______", tdBold));
            summaryTable.addCell(createBorderedCell("Absent Count: ______", tdBold));
            summaryTable.addCell(createBorderedCell("Malpractice: NIL / ____", tdBold));
            document.add(summaryTable);

            Paragraph signLines = new Paragraph("\n\nInvigilator Signature: _______________________                   Chief Supdt. Signature: _______________________", tdBold);
            document.add(signLines);
        }

        document.close();
        return baos.toByteArray();
    }

    private PdfPCell createBorderedCell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(4);
        c.setBackgroundColor(new Color(248, 248, 248));
        return c;
    }

    private PdfPCell createAttCell(String text, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(3);
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    /**
     * Condenses consecutive register numbers into "VJC23EC001 - VJC23EC006" ranges
     * exactly like the real college seating arrangement PDF!
     */
    private String formatRegisterNumbersCondensed(List<Student> students) {
        if (students.isEmpty()) return "";
        List<String> tokens = new ArrayList<>();

        int i = 0;
        while (i < students.size()) {
            int j = i;
            String baseReg = students.get(i).getRegisterNumber();
            String prefix = extractPrefix(baseReg);
            Long currentNum = extractNumber(baseReg);

            if (currentNum == null) {
                tokens.add(baseReg);
                i++;
                continue;
            }

            while (j + 1 < students.size()) {
                String nextReg = students.get(j + 1).getRegisterNumber();
                String nextPrefix = extractPrefix(nextReg);
                Long nextNum = extractNumber(nextReg);

                if (prefix.equals(nextPrefix) && nextNum != null && nextNum == currentNum + (j + 1 - i)) {
                    j++;
                } else {
                    break;
                }
            }

            int count = j - i + 1;
            if (count >= 3) {
                // Range format: VJC23EC001 - VJC23EC006
                tokens.add(students.get(i).getRegisterNumber() + " - " + students.get(j).getRegisterNumber());
            } else {
                for (int k = i; k <= j; k++) {
                    tokens.add(students.get(k).getRegisterNumber());
                }
            }
            i = j + 1;
        }

        return String.join(", ", tokens);
    }

    private String extractPrefix(String reg) {
        if (reg == null) return "";
        return reg.replaceAll("\\d+$", "");
    }

    private Long extractNumber(String reg) {
        if (reg == null) return null;
        try {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)$").matcher(reg);
            if (m.find()) {
                return Long.parseLong(m.group(1));
            }
        } catch (Exception ignored) {}
        return null;
    }
}
